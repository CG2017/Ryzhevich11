#include <stdio.h>
#include <cmath>
#include <iostream>
#include <fstream>
#include <vector>

struct rgb {
	unsigned int red;
	unsigned int green;
	unsigned int blue;
	rgb(unsigned int r, unsigned int g, unsigned int b): red(r), green(g), blue(b) {}
	rgb(): red(0), green(0), blue(0) {}
};

std::ofstream fout("output.txt");
const int HEADER_SIZE = 6;
char header[HEADER_SIZE + 1];
unsigned short size[2];
unsigned int glob_table_size = 0, bits_per_pixel = 0;
bool sort = false, has_glob_table = false;
unsigned char bg_color_idx = 0;
unsigned char pix_asp_ratio = 0;
std::vector<rgb> global_table;
std::vector<rgb> colors;

void read_color_table(FILE *gif, std::vector<rgb> &table_vector, unsigned int size) {
	table_vector.resize(size);
	unsigned char *table = new unsigned char[size * 3];
	fread(table, sizeof(unsigned char), size * 3, gif);
	fout << "No\tR\tG\tB" << std::endl;
	for (unsigned int i = 0; i < size; i++) {
		table_vector[i] = rgb((unsigned int)table[3*i], (unsigned int)table[3*i+1], (unsigned int)table[3*i+2]);
		fout << i << ":\t" << table_vector[i].red << "\t" << table_vector[i].green << "\t" << table_vector[i].blue << std::endl;
	}
	delete []table;
}

void read_plain_text(FILE *gif, unsigned int size) {
	fout << "\nPLAIN TEXT EXTENSION" << std::endl;
	fout << "Text:" << std::endl;
	unsigned char part_size = (unsigned char) size;
	do {
		char *data = new char[(unsigned int) part_size + 1];
		fread(data, sizeof(char), (unsigned int)part_size, gif);
		data[part_size] = 0;
		fout << data << std::endl;
		delete []data;
		fread(&part_size, sizeof(unsigned char), 1, gif);
		size += (unsigned int)part_size;
	} while(part_size != 0);
	fout << "Size: " << size << std::endl;
}

void read_comment(FILE *gif, unsigned int size) {
	fout << "\nCOMMENT EXTENSION" << std::endl;
	fout << "Comment:" << std::endl;
	unsigned char part_size = (unsigned char) size;
	do {
		char *data = new char[(unsigned int) part_size + 1];
		fread(data, sizeof(char), (unsigned int)part_size, gif);
		data[part_size] = 0;
		fout << data << std::endl;
		delete []data;
		fread(&part_size, sizeof(unsigned char), 1, gif);
		size += (unsigned int)part_size;
	} while(part_size != 0);
	fout << "Size: " << size << std::endl;
}

void read_application_extension(FILE *gif, unsigned int size) {
	fout << "\nAPPLICATION EXTENSION" << std::endl;
	if (size != 11) {
		fout << "Unknown application extension." << std::endl;
		fout << "Size: " << size << std::endl;
		char *data = new char[size + 1];
		fread(data, sizeof(char), size + 1, gif);
		fout << "Data: " << data << std::endl;
		delete []data;
		return;
	}
	char app_code[4], app_id[9];
	memset(app_id, 0, 9);
	fread(app_id, sizeof(char), 8, gif);
	fread(app_code, sizeof(char), 4, gif);
	app_code[3] = 0;
	fout << "Application identifier: " << app_id << std::endl;
	fout << "Application authentication code: " << app_code << std::endl;
}

void read_graphics_control(FILE *gif, unsigned int size) {
	fout << "\nGRAPHICS CONTROL EXTENSION" << std::endl;
	if (size != 4) {
		fout << "Unknown graphics control extension." << std::endl;
		fout << "Size: " << size << std::endl;
		char *data = new char[size + 1];
		fread(data, sizeof(char), size + 1, gif);
		fout << "Data: " << data << std::endl;
		delete []data;
		return;
	}
	unsigned char packed = 0;
	fread(&packed, sizeof(unsigned char), 1, gif);
	unsigned int disposal_method = (unsigned int)((packed & 28) >> 2);
	bool user_input_flag = ((packed & 2) > 0), transp_color_flag = ((packed & 1) > 0);
	fout << "Disposal method: " << disposal_method << std::endl;
	fout << "User input flag: " << user_input_flag << std::endl;
	fout << "Transparent color flag: " << transp_color_flag << std::endl;
	unsigned short delay_time = 0;
	fread(&delay_time, sizeof(unsigned short), 1, gif);
	fout << "Delay time: " << delay_time << std::endl;
	unsigned char transp_color_idx = 0;
	fread(&transp_color_idx, sizeof(unsigned char), 1, gif);
	fout << "Transparent color idx: " << (unsigned int) transp_color_idx << std::endl;
	fread(&packed, sizeof(unsigned char), 1, gif);
}

void read_extension(FILE *gif) {
	unsigned char type = 0, size = 0;
	fread(&type, sizeof(unsigned char), 1, gif);
	fread(&size, sizeof(unsigned char), 1, gif);
	switch (type) {
		case 0x01: {
			read_plain_text(gif, (unsigned int) size);
			return;
		}
		case 0xF9: {
			read_graphics_control(gif, (unsigned int) size);
			return;
		}
		case 0xFE: {
			read_comment(gif, (unsigned int) size);
			return;
		}
		case 0xFF: {
			read_application_extension(gif, (unsigned int) size);
			return;
		}
	}
}

void decode_image_data(std::vector<unsigned char> &image_data, unsigned int min_code_size, 
					   bool has_local_table, std::vector<rgb> &local_table) {
	std::vector<unsigned int> codes;
	std::vector<std::vector<unsigned int> > dictionary;
	unsigned int base_size = 1 << min_code_size;
	unsigned int next_code = base_size + 2;
	unsigned int clear_code = base_size;
	unsigned int eoi_code = base_size + 1;
	unsigned int step = min_code_size + 1;
	dictionary.resize(base_size + 2);
	for (unsigned int i = 0; i < base_size; i++) {
		dictionary.push_back(std::vector<unsigned int> (1, i));
	}
	dictionary.push_back(std::vector<unsigned int>());
	dictionary.push_back(std::vector<unsigned int>());
	bool stop = false, first = true;
	unsigned int aaa = 0;
	unsigned int prev_code = 0, cur_code = 0, step_i = 0;
	for (unsigned int i = 0; i < image_data.size(); i++) {
		unsigned char mask = 1;
		unsigned char cur_char = image_data[i];
		for (unsigned int j = 0; j < 8; j++) {
			aaa++;
			unsigned int plus = (cur_char & mask) ? 1 : 0;
			cur_code += (plus << step_i);
			mask <<= 1;
			step_i++;
			if (step_i == step) {
				step_i = 0;
				if (cur_code >= next_code) {
					std::vector<unsigned int> new_code_v(dictionary[prev_code]);
					new_code_v.push_back(dictionary[prev_code][0]);
					dictionary.push_back(new_code_v);
					next_code++;
				}
				else if (cur_code == clear_code || cur_code == eoi_code) {
					for (auto _c : codes) {
						for (auto c : dictionary[_c]) {
							has_local_table ? colors.push_back(local_table[c]) : colors.push_back(global_table[c]);
						}
					}
					codes = std::vector<unsigned int>();
					dictionary = std::vector<std::vector<unsigned int> >();
					base_size = 1 << min_code_size;
					next_code = base_size + 2;
					clear_code = base_size;
					eoi_code = base_size + 1;
					step = min_code_size + 1;
					for (unsigned int i = 0; i < base_size; i++) {
						dictionary.push_back(std::vector<unsigned int> (1, i));
					}
					dictionary.push_back(std::vector<unsigned int>());
					dictionary.push_back(std::vector<unsigned int>());
					if (cur_code == eoi_code) {
						stop = true;
						break;
					}
					cur_code = 0;
					prev_code = 0;
					step_i = 0;
					first = true;
				} else {
					if (!first) {
						std::vector<unsigned int> new_code_v(dictionary[prev_code]);
						new_code_v.push_back(dictionary[cur_code][0]);
						dictionary.push_back(new_code_v);
						next_code++;
					}
					codes.push_back(cur_code);
					first = false;
				}
				prev_code = cur_code;
				cur_code = 0;
				if (next_code >= (1 << step) && step < 12) {
					step++;
				}
			}
		}
		if (stop) break;
	}
}

void read_frame(FILE *gif) {
	fout << "\nIMAGE DESCRIPTOR" << std::endl;
	unsigned short image_size[4];
	fread(image_size, sizeof(unsigned short), 4, gif);
	fout << "Left: " << image_size[0] << " Top: " << image_size[1] << " Size: " << image_size[2] << "x" << image_size[3] << std::endl;
	unsigned char packed = 0;
	fread(&packed, sizeof(unsigned char), 1, gif);
	bool has_local_table = ((packed & 128) > 0), interlace = ((packed & 64) > 0), loc_sort = ((packed & 32) > 0);
	unsigned int local_table_size = has_local_table ? (1 << ((unsigned int)(packed & 7) + 1)) : 0;
	fout << "Has local color table: " << has_local_table << std::endl;
	fout << "Interlace flag: " << interlace << std::endl;
	fout << "Sorted colors: " << loc_sort << std::endl;
	fout << "Size of local color table: " << local_table_size << std::endl;
	std::vector<rgb> local_table;
	if (has_local_table) {
		fout << "\nLOCAL COLOR TABLE" << std::endl;
		read_color_table(gif, local_table, local_table_size);
	}
	fout << "\nIMAGE DATA" << std::endl;
	unsigned char lzw_min_code_size = 0;
	unsigned int image_data_size = 0;
	fread(&lzw_min_code_size, sizeof(unsigned char), 1, gif);
	fout << "LZW minimum code size: " << (unsigned int)lzw_min_code_size << std::endl;
	std::vector<unsigned char> image_data;
	while(true) {
		unsigned char part_size = 0;
		fread(&part_size, sizeof(unsigned char), 1, gif);
		if (part_size == 0) break;
		image_data_size += (unsigned int) part_size;
		unsigned char *data = new unsigned char[part_size];
		fread(data, sizeof(unsigned char), (unsigned int)part_size, gif);
		for (unsigned int i = 0; i < part_size; i++) {
			image_data.push_back(data[i]);
		}
		delete []data;
	}
	fout << "Image data size: " << image_data_size << std::endl;
	decode_image_data(image_data, lzw_min_code_size, has_local_table, local_table);
}

void build_color_diagram() {
	std::vector<unsigned int> red(256, 0), green(256, 0), blue(256, 0);
	for (auto &c : colors) {
		red[c.red]++;
		green[c.green]++;
		blue[c.blue]++;
	}
	fout << "\n=======================================" << std::endl;
	fout << "\nCOLOR DIAGRAM" << std::endl;
	fout << "Val\tRed\tGreen\tBlue" << std::endl;
	for (unsigned int i = 0; i < 256; i++) {
		fout << i << '\t' << red[i] << '\t' << green[i] << '\t' << blue[i] << '\t' << std::endl;
	}
	double avg_red = 0, avg_green = 0, avg_blue = 0;
	for (unsigned int i = 1; i < 256; i++) {
		avg_red += (double)(red[i] * i) / (double) colors.size();
		avg_green += (double)(green[i] * i) / (double) colors.size();
		avg_blue += (double)(blue[i] * i) / (double) colors.size();
	}
	fout << "\n=======================================" << std::endl;
	fout << "\nAVERAGE COLOR" << std::endl;
	fout << "Red: " << avg_red << std::endl;
	fout << "Green: " << avg_green << std::endl;
	fout << "Blue: " << avg_blue << std::endl;
}

int main(int argc, char **argv) {
	if (argc != 2) {
		std::cout << "Invalid parameters!\nParameters should be as follows:\n<app_name> <gif_file_name>" << std::endl;
		return 1;
	}
	FILE *gif = fopen(argv[1], "rb");
	if (!gif) {
		std::cout << "Can't open file " << argv[1] << std::endl;
		return 1;
	}

	fout << "HEADER BLOCK" << std::endl;
	memset(header, 0, HEADER_SIZE + 1);
	fread(header, sizeof(char), HEADER_SIZE, gif);
	fout << "GIF version: " << header << std::endl;

	fout << "\nLOGICAL SCREEN DESCRIPTOR" << std::endl;
	fread(size, sizeof(short), 2, gif);
	fout << "Size: " << size[0] << "x" << size[1] << std::endl;

	unsigned char packed = 0;
	fread(&packed, sizeof(char), 1, gif);

	glob_table_size = 1 << ((unsigned int)(packed & 7) + 1); bits_per_pixel = (unsigned int)((packed & 112) >> 4) + 1;
	sort = ((packed & 8) > 0); has_glob_table = ((packed & 128) > 0);
	if (!has_glob_table) glob_table_size = 0;
	fout << "Has global color table: " << has_glob_table << std::endl;
	fout << "Bits per pixel: " << bits_per_pixel << std::endl;
	fout << "Sorted colors: " << sort << std::endl;
	fout << "Size of global color table: " << glob_table_size << std::endl;

	fread(&bg_color_idx, sizeof(unsigned char), 1, gif);
	fout << "Background color index: " << (unsigned int)bg_color_idx << std::endl;

	fread(&pix_asp_ratio, sizeof(unsigned char), 1, gif);
	fout << "Pixel aspect ratio: " << (unsigned int)pix_asp_ratio << std::endl;
	
	if (has_glob_table) {
		fout << "\nGLOBAL COLOR TABLE" << std::endl;
		read_color_table(gif, global_table, glob_table_size);
	}

	bool stop = false;
	while (!stop) {
		unsigned char flag = 0;
		fread(&flag, sizeof(unsigned char), 1, gif);
		switch (flag) {
			case 0x3B: {
				fout << "\nEND OF FILE" << std::endl;
				stop = true;
			} break;
			case 0x21: {
				read_extension(gif);
			} break;
			case 0x2C: {
				read_frame(gif);
			} break;
		}
	}
	build_color_diagram();
	fclose(gif);
	fout.close();
	return 0;
}