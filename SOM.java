package programowanie;

import java.awt.*;

public class SOM {

    private Vec2D[][] neurony;
    private int rows, cols;

    // Ustalamy stałe parametry uczenia na sztywno, 
    // aby sieć nie wygaszała się do zera i nie skakała nagle przy zmianie fazy
    private final double eta = 0.04; 
    private final double S = 2.5;    

    public SOM(int w, int h) {
        cols = w;
        rows = h;
        neurony = new Vec2D[h][w];
        resetToCenter();
    }

    public void resetToCenter() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                neurony[i][j] = new Vec2D(0, 0);
            }
        }
    }

    // Pusta metoda - celowo nic nie robi, żeby nie psuć płynnego ruchu
    public void refreshLearningParams() {
        // Parametry są teraz stałe (final), brak nagłych skoków promienia sąsiedztwa
    }

    public void ucz(Vec2D input) {
        int bx = 0;
        int by = 0;
        double best = Double.MAX_VALUE;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double dx = neurony[i][j].x - input.x;
                double dy = neurony[i][j].y - input.y;
                double d = dx * dx + dy * dy;

                if (d < best) {
                    best = d;
                    bx = i;
                    by = j;
                }
            }
        }

        double s2 = 2 * S * S;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double ds = (i - bx) * (i - bx) + (j - by) * (j - by);
                double g = Math.exp(-ds / s2);

                // Powolne, stabilne przyciąganie neuronów do nowego celu
                neurony[i][j].x += eta * g * (input.x - neurony[i][j].x);
                neurony[i][j].y += eta * g * (input.y - neurony[i][j].y);
            }
        }

        // USUNIĘTO wygaszanie eta *= 0.9996 i S *= 0.9996!
    }

    public void draw(Graphics2D g, int x0, int y0, int w, int h) {
        g.setStroke(new BasicStroke(1.2f));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = mapX(x0, w, h, neurony[i][j]);
                int y = mapY(y0, h, neurony[i][j]);

                g.setColor(new Color(30, 120, 255));
                if (i + 1 < rows) {
                    g.drawLine(x, y, mapX(x0, w, h, neurony[i + 1][j]), mapY(y0, h, neurony[i + 1][j]));
                }
                if (j + 1 < cols) {
                    g.drawLine(x, y, mapX(x0, w, h, neurony[i][j + 1]), mapY(y0, h, neurony[i][j + 1]));
                }

                g.setColor(Color.RED);
                g.fillOval(x - 2, y - 2, 4, 4);
            }
        }
    }

    private int mapX(int x0, int w, int h, Vec2D v) {
        int size = (int) (h * 0.75);
        int cx = x0 + w / 2;
        return cx + (int) (v.x * (size / 2));
    }

    private int mapY(int y0, int h, Vec2D v) {
        int size = (int) (h * 0.75);
        int cy = y0 + h / 2;
        return cy + (int) (v.y * (size / 2));
    }
}