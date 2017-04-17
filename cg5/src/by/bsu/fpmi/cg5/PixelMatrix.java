package by.bsu.fpmi.cg5;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class PixelMatrix {

    public int x1, x2, y1, y2, r;

    private boolean[][] pixels;
    private int size = 0;
    private boolean isLine = true;
    public static final int MAX_SIZE = 100;

    public static final int STEP_ALGO = 0;
    public static final int DDA_ALGO = 1;
    public static final int BRESENHAM_ALGO = 2;

    public void setSize(int size) {
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
        if (size < 0) {
            size = 0;
        }
        this.pixels = new boolean[size][size];
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public boolean hasPixel(int x, int y) {
        return (x >= 0 && x < size && y >= 0 && y < size) && pixels[x][y];
    }

    public boolean isLine() {
        return isLine;
    }

    public boolean isCircle() {
        return !isLine;
    }

    private void setPixel(int x, int y) {
        if (!(x >= 0 && x < size && y >= 0 && y < size)){
            return;
        }
        pixels[x][y] = true;
    }

    public void calculateLine(int algo, int x1, int y1, int x2, int y2) {
        if (!(x1 >= 0 && x1 < size && x2 >= 0 && x2 < size && y1 >= 0 && y1 < size && y2 >= 0 && y2 < size)) {
            return;
        }
        this.x1 = x1; this.x2 = x2; this.y1 = y1; this.y2 = y2;
        isLine = true;
        reset();
        switch (algo) {
            case STEP_ALGO: calculateStepLine(x1, y1, x2, y2); break;
            case DDA_ALGO: calculateDDALine(x1, y1, x2, y2); break;
            case BRESENHAM_ALGO: calculateBresenhamLine(x1, y1, x2, y2); break;
        }
    }

    public void calculateCircle(int x1, int y1, int r) {
        this.x1 = x1; this.y1 = y1; this.r = r;
        isLine = false;
        int x = 0;
        int y = r;
        setPixel(x1 - x, y1 - y);
        setPixel(x1 + x, y1 + y);
        setPixel(x1 - x, y1 + y);
        setPixel(x1 + x, y1 - y);
        while (y > 0)
        {
            int xr = x + 1;
            int xv = x;
            int xd = x + 1;
            int yr = y;
            int yv = y - 1;
            int yd = y - 1;
            double diffR = Math.abs(xr * xr + yr * yr - r * r);
            double diffV = Math.abs(xv * xv + yv * yv - r * r);
            double diffD = Math.abs(xd * xd + yd * yd - r * r);
            if (diffR < diffD && diffR < diffV)
            {
                x = xr;
                y = yr;
                setPixel(x1 - x, y1 - y);
                setPixel(x1 + x, y1 + y);
                setPixel(x1 - x, y1 + y);
                setPixel(x1 + x, y1 - y);
            }
            else if (diffV < diffD && diffV < diffD)
            {
                x = xv;
                y = yv;
                setPixel(x1 - x, y1 - y);
                setPixel(x1 + x, y1 + y);
                setPixel(x1 - x, y1 + y);
                setPixel(x1 + x, y1 - y);
            }
            else
            {
                x = xd;
                y = yd;
                setPixel(x1 - x, y1 - y);
                setPixel(x1 + x, y1 + y);
                setPixel(x1 - x, y1 + y);
                setPixel(x1 + x, y1 - y);
            }

        }
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            for (int j= 0; j < size; j++) {
                pixels[i][j] = false;
            }
        }
    }

    private void calculateStepLine(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int bufX = x1, bufY = y1;
            x1 = x2; y1 = y2;
            x2 = bufX; y2 = bufY;
        }
        int dx = x2 - x1;
        int dy = y2 - y1;
        for (int x = x1; x < x2; x++) {
            int y = y1 + dy * (x - x1) / dx;
            setPixel(x, y);
        }
    }

    private void calculateDDALine(int _x1, int _y1, int _x2, int _y2) {
        int steps = Math.max(Math.abs(_x2 - _x1), Math.abs(_y2 - _y1)) + 1;
        double x1 = (double) _x1, x2 = (double) _x2, y1 = (double) _y1, y2 = (double) _y2;
        double x = x1, y = y1, dx = (x2 - x1) / (double) steps, dy = (y2 - y1) / (double) steps;
        for (int i = 0; i < steps; i++) {
            int _x = (int) x, _y = (int) y;
            setPixel(_x, _y);
            x += dx; y += dy;
        }
    }

    private void calculateBresenhamLine(int x1, int y1, int x2, int y2) {
        int x, y, dx, dy, dx1, dy1, px, py, xe, ye;
        dx = x2 - x1; dy = y2- y1;
        dx1 = Math.abs(dx); dy1 = Math.abs(dy);
        px = 2 * dy1 - dx1; py = 2 * dx1 - dy1;

        if (dy1 <= dx1) {
            if (dx >= 0) {
                x = x1; y = y1; xe = x2;
            } else {
                x = x2; y = y2; xe = x1;
            }
            setPixel(x, y);
            while (x < xe) {
                x += 1;
                if (px < 0) {
                    px = px + 2 * dy1;
                } else {
                    if((dx < 0 && dy < 0) || (dx > 0 && dy > 0)) {
                        y += 1;
                    } else {
                        y -= 1;
                    }
                    px = px + 2 * (dy1 - dx1);
                }
                setPixel(x, y);
            }
        } else {
            if (dy >= 0) {
                x = x1; y = y1; ye = y2;
            } else {
                x = x2; y = y2; ye = y1;
            }
            setPixel(x, y);
            while (y < ye) {
                y += 1;
                if(py <= 0) {
                    py = py + 2 * dx1;
                } else {
                    if((dx < 0 && dy < 0) || (dx > 0 && dy > 0)) {
                        x += 1;
                    } else {
                        x -= 1;
                    }
                    py = py + 2 * (dx1 - dy1);
                }
                setPixel(x, y);
            }
        }
    }
}
