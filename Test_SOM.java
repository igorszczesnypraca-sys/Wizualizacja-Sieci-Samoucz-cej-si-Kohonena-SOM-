package programowanie;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Test_SOM extends JFrame {

    enum ShapeType {
        KOLO,
        PROSTOKAT,
        GWIAZDA
    }

    enum Phase {
        MORPH_TO_LEFT,     
        MORPH_TO_RIGHT     
    }

    private ShapeType leftShape = ShapeType.KOLO;
    private ShapeType rightShape = ShapeType.PROSTOKAT;

    private Phase phase = Phase.MORPH_TO_LEFT;
    private SOM som = new SOM(35, 35);
    private Random r = new Random();
    private boolean running = false;

    private int frameCounter = 0;
    // 5 sekund przy 60 FPS to dokładnie 300 klatek
    private final int FIVE_SECONDS_FRAMES = 300; 

    private JSlider speedSlider = new JSlider(1, 5000, 300);

    class Panel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();
            int panelW = w / 3;

            // ==========================================
            // UCZENIE SOM (Prawdziwy, powolny morfing)
            // ==========================================
            if (running) {
                int steps = speedSlider.getValue();

                for (int i = 0; i < steps; i++) {
                    switch (phase) {
                        case MORPH_TO_LEFT -> {
                            som.ucz(random(leftShape));
                        }
                        case MORPH_TO_RIGHT -> {
                            som.ucz(random(rightShape));
                        }
                    }
                }

                frameCounter++;
                
                if (frameCounter >= FIVE_SECONDS_FRAMES) {
                    frameCounter = 0;
                    
                    // USUNIĘTO som.refreshLearningParams()! 
                    // Nie resetujemy parametrów sieci, dzięki czemu zachowuje ona kształt
                    // i powoli, plastycznie przeciąga neurony w nowe miejsce.
                    
                    if (phase == Phase.MORPH_TO_LEFT) {
                        phase = Phase.MORPH_TO_RIGHT;
                    } else {
                        phase = Phase.MORPH_TO_LEFT;
                    }
                }
            }

            // Rysowanie
            drawFrameBackground(g2, panelW, h);
            drawShape(g2, leftShape, 0, 0, panelW, h);
            drawShape(g2, rightShape, panelW * 2, 0, panelW, h);
            som.draw(g2, panelW, 0, panelW, h);
            drawTimer(g2, panelW, w);
        }

        private void drawFrameBackground(Graphics2D g2, int panelW, int h) {
            g2.setColor(new Color(235, 235, 235));
            g2.fillRect(0, 0, panelW, h);
            g2.fillRect(panelW * 2, 0, panelW, h);

            g2.setColor(new Color(250, 250, 250));
            g2.fillRect(panelW, 0, panelW, h);

            g2.setColor(Color.GRAY);
            g2.drawLine(panelW, 0, panelW, h);
            g2.drawLine(panelW * 2, 0, panelW * 2, h);
        }

        private void drawShape(Graphics2D g2, ShapeType type, int x0, int y0, int w, int h) {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.0f));

            int size = (int) (h * 0.75); 
            int cx = x0 + w / 2;
            int cy = y0 + h / 2;

            switch (type) {
                case KOLO -> {
                    int rPix = (int) (0.8 * size / 2);
                    g2.drawOval(cx - rPix, cy - rPix, rPix * 2, rPix * 2);
                }
                case PROSTOKAT -> {
                    int rw = (int) (0.8 * size / 2);
                    int rh = (int) (0.5 * size / 2);
                    g2.drawRect(cx - rw, cy - rh, rw * 2, rh * 2);
                }
                case GWIAZDA -> {
                    Polygon p = new Polygon();
                    for (int i = 0; i < 10; i++) {
                        double a = Math.toRadians(-90 + i * 36);
                        double rr = (i % 2 == 0) ? 0.8 : 0.35;
                        
                        int px = cx + (int) (Math.cos(a) * (rr * size / 2));
                        int py = cy + (int) (Math.sin(a) * (rr * size / 2));
                        p.addPoint(px, py);
                    }
                    g2.drawPolygon(p);
                }
            }
        }

        private void drawTimer(Graphics2D g2, int panelW, int totalW) {
            double remainingSeconds = 5.0 - (frameCounter * 0.016666);
            if (remainingSeconds < 0 || !running) {
                remainingSeconds = 5.0;
            }

            String timerText = String.format("%.2f s", remainingSeconds);
            g2.setFont(new Font("Monospaced", Font.BOLD, 22));
            int textWidth = g2.getFontMetrics().stringWidth(timerText);

            int x = (panelW * 2) - textWidth - 20;
            int y = 40; 

            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(x - 10, y - 24, textWidth + 20, 32, 10, 10);

            g2.setColor(new Color(40, 40, 40));
            g2.drawString(timerText, x, y);
        }
    }

    public Test_SOM() {
        super("SOM");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Panel panel = new Panel();

        speedSlider.setBorder(BorderFactory.createTitledBorder("Prędkość uczenia"));
        add(speedSlider, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        JPanel left = new JPanel();
        JPanel center = new JPanel();
        JPanel right = new JPanel();

        JButton start = new JButton("START");
        center.add(start);

        JButton l1 = new JButton("Koło");
        JButton l2 = new JButton("Prostokąt");
        JButton l3 = new JButton("Gwiazda");
        left.add(l1); left.add(l2); left.add(l3);

        JButton r1 = new JButton("Koło");
        JButton r2 = new JButton("Prostokąt");
        JButton r3 = new JButton("Gwiazda");
        right.add(r1); right.add(r2); right.add(r3);

        JPanel bottom = new JPanel(new GridLayout(1, 3));
        bottom.add(left); bottom.add(center); bottom.add(right);
        add(bottom, BorderLayout.SOUTH);

        l1.addActionListener(e -> leftShape = ShapeType.KOLO);
        l2.addActionListener(e -> leftShape = ShapeType.PROSTOKAT);
        l3.addActionListener(e -> leftShape = ShapeType.GWIAZDA);

        r1.addActionListener(e -> rightShape = ShapeType.KOLO);
        r2.addActionListener(e -> rightShape = ShapeType.PROSTOKAT);
        r3.addActionListener(e -> rightShape = ShapeType.GWIAZDA);

        start.addActionListener(e -> {
            som.resetToCenter(); // Reset tylko raz na samym początku programu
            frameCounter = 0;               
            phase = Phase.MORPH_TO_LEFT;    
            running = true;
        });

        new Timer(16, e -> panel.repaint()).start();

        setSize(1400, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Vec2D random(ShapeType t) {
        while (true) {
            double x = r.nextDouble() * 2 - 1;
            double y = r.nextDouble() * 2 - 1;

            switch (t) {
                case KOLO -> {
                    if (x * x + y * y < 0.64) 
                        return new Vec2D(x, y);
                }
                case PROSTOKAT -> {
                    if (Math.abs(x) < 0.8 && Math.abs(y) < 0.5)
                        return new Vec2D(x, y);
                }
                case GWIAZDA -> {
                    Polygon p = starPolygon();
                    if (p.contains(x * 100, y * 100))
                        return new Vec2D(x, y);
                }
            }
        }
    }

    private Polygon starPolygon() {
        Polygon p = new Polygon();
        for (int i = 0; i < 10; i++) {
            double a = Math.toRadians(-90 + i * 36);
            double rr = (i % 2 == 0) ? 80.0 : 35.0; 
            p.addPoint((int) (Math.cos(a) * rr), (int) (Math.sin(a) * rr));
        }
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Test_SOM::new);
    }
}