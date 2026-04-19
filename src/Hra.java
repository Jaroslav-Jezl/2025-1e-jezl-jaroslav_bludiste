import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.geom.Point2D;

public class Hra extends JFrame {
    Mapa mojeMapa = new Mapa();
    int aktualniLevel = 1;
    int hracR = 1, hracS = 1;
    int tikRyba = 0;
    Color barvaHrace = Color.CYAN;
    Random rng = new Random();
    ArrayList<Projektil> strely = new ArrayList<>();
    ArrayList<Ryba> ryby = new ArrayList<>();

    boolean hraVyhrana = false;
    Rectangle btnLvl1, btnLvl2, btnLvl3, btnKonec;

    int stavFaze = 0;
    int cyklusIndex = 0;
    double[] delkyPauz = {0.5, 1.5, 2.5};

    public Hra() {
        setTitle("MAZE MASTER: FULL GAME");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel platno = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g2);
                this.setBackground(Color.BLACK);

                mojeMapa.vykresli(g2, getWidth(), getHeight(), aktualniLevel);

                int px = hracS * mojeMapa.sirkaPole;
                int py = hracR * mojeMapa.vyskaPole;

                // Vykreslení nepřátel podle levelu
                if (aktualniLevel == 2) {
                    for (Ryba r : ryby) r.vykresli(g2, mojeMapa.sirkaPole, mojeMapa.vyskaPole);
                }
                if (aktualniLevel == 3) {
                    for (Projektil p : strely) p.vykresli(g2, mojeMapa.sirkaPole, mojeMapa.vyskaPole);
                    mojeMapa.vykresliMlhu(g2, hracS, hracR, getWidth(), getHeight());
                }

                // Vykreslení hráče
                g2.setColor(barvaHrace);
                g2.fillRoundRect(px + 6, py + 6, mojeMapa.sirkaPole - 12, mojeMapa.vyskaPole - 12, 10, 10);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(px + 6, py + 6, mojeMapa.sirkaPole - 12, mojeMapa.vyskaPole - 12, 10, 10);

                if (hraVyhrana) vykresliVitezneMenu(g2);
            }
        };

        platno.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!hraVyhrana) return;
                Point p = e.getPoint();
                if (btnLvl1.contains(p)) { prepniNaLevel(1); hraVyhrana = false; }
                else if (btnLvl2.contains(p)) { prepniNaLevel(2); hraVyhrana = false; }
                else if (btnLvl3.contains(p)) { prepniNaLevel(3); hraVyhrana = false; }
                else if (btnKonec.contains(p)) { System.exit(0); }
                repaint();
            }
        });

        add(platno);

        // Časovač střídání fází pro Level 3
        Timer fázovač = new Timer(500, null);
        fázovač.addActionListener(e -> {
            if (aktualniLevel != 3 || hraVyhrana) return;
            if (stavFaze == 0) {
                mojeMapa.strelciAktivni = false;
                stavFaze = 1;
                fázovač.setInitialDelay((int)(delkyPauz[cyklusIndex] * 1000));
                fázovač.restart();
            } else {
                mojeMapa.strelciAktivni = true;
                stavFaze = 0;
                cyklusIndex = (cyklusIndex + 1) % 3;
                fázovač.setInitialDelay(1000);
                fázovač.restart();
            }
        });
        fázovač.start();

        // Hlavní smyčka pohybu a kolizí
        new Timer(50, e -> {
            if (hraVyhrana) return;
            if (aktualniLevel == 2) {
                tikRyba++;
                if (tikRyba % 2 == 0) {
                    for (Ryba r : ryby) {
                        r.pohyb(mojeMapa.rozlozeni);
                        if (r.r == hracR && r.s == hracS) resetuj();
                    }
                }
            }
            if (aktualniLevel == 3) {
                if (mojeMapa.strelciAktivni && Math.random() < 0.25) {
                    strely.add(new Projektil(2, 2, 1));
                    strely.add(new Projektil(2, 8, 1));
                    strely.add(new Projektil(5, 0, 1));
                    strely.add(new Projektil(5, 8, 1));
                    strely.add(new Projektil(5, 14, 1));
                    strely.add(new Projektil(7, 5, 1));
                }
                for (int i = strely.size() - 1; i >= 0; i--) {
                    Projektil p = strely.get(i);
                    p.s += p.smer;
                    if (p.r == hracR && p.s == hracS) resetuj();
                    if (p.s < 0 || p.s >= 20 || mojeMapa.rozlozeni[p.r][p.s] == 1) strely.remove(i);
                }
            }
            repaint();
        }).start();

        // Časovač lávy
        new Timer(1000, e -> {
            if (hraVyhrana) return;
            mojeMapa.lavaAktivni = !mojeMapa.lavaAktivni;
            if (mojeMapa.lavaAktivni && mojeMapa.rozlozeni[hracR][hracS] == 5) resetuj();
            repaint();
        }).start();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (hraVyhrana) return;
                int r = hracR, s = hracS;
                if (e.getKeyCode() == KeyEvent.VK_UP) r--;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) r++;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) s--;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) s++;

                if (r >= 0 && r < 11 && s >= 0 && s < 20) {
                    if (mojeMapa.rozlozeni[r][s] != 1) {
                        hracR = r; hracS = s;
                        if (mojeMapa.rozlozeni[r][s] == 2) {
                            if (aktualniLevel < 3) prepniNaLevel(aktualniLevel + 1);
                            else hraVyhrana = true;
                        }
                    }
                }
                repaint();
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    void vykresliVitezneMenu(Graphics2D g) {
        int w = getWidth(), h = getHeight();
        int mw = (int)(w * 0.75), mh = (int)(h * 0.75);
        int mx = (w - mw) / 2, my = (h - mh) / 2;
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(30, 30, 50));
        g.fillRoundRect(mx, my, mw, mh, 30, 30);
        g.setColor(new Color(0, 255, 255));
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(mx, my, mw, mh, 30, 30);
        g.setFont(new Font("Segoe UI", Font.BOLD, 50));
        g.drawString("VÍTĚZSTVÍ!", mx + mw/2 - 140, my + 80);
        int bw = 180, bh = 55;
        int bx = mx + mw/2 - bw/2;
        btnLvl1 = new Rectangle(bx - 210, my + 200, bw, bh);
        btnLvl2 = new Rectangle(bx, my + 200, bw, bh);
        btnLvl3 = new Rectangle(bx + 210, my + 200, bw, bh);
        btnKonec = new Rectangle(bx, my + 310, bw, bh);
        vykresliTlacitko(g, btnLvl1, "LEVEL 1", new Color(46, 204, 113));
        vykresliTlacitko(g, btnLvl2, "LEVEL 2", new Color(52, 152, 219));
        vykresliTlacitko(g, btnLvl3, "LEVEL 3", new Color(231, 76, 60));
        vykresliTlacitko(g, btnKonec, "KONEC HRY", Color.DARK_GRAY);
    }

    void vykresliTlacitko(Graphics2D g, Rectangle r, String text, Color c) {
        g.setColor(c);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 15, 15);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString(text, r.x + 40, r.y + 35);
    }

    void prepniNaLevel(int lvl) {
        aktualniLevel = lvl;
        ryby.clear(); strely.clear();
        if (lvl == 1) mojeMapa.rozlozeni = mojeMapa.level1;
        else if (lvl == 2) {
            mojeMapa.rozlozeni = mojeMapa.level2;
            ryby.add(new Ryba(3, 5, false));
            ryby.add(new Ryba(5, 10, false));
        } else if (lvl == 3) mojeMapa.rozlozeni = mojeMapa.level3;
        resetuj();
    }

    void resetuj() {
        hracR = 1; hracS = 1;
        barvaHrace = new Color(rng.nextInt(156)+100, rng.nextInt(156)+100, rng.nextInt(156)+100);
        repaint();
    }

    class Ryba {
        int r, s, sm = 1; boolean vert;
        Ryba(int r, int s, boolean v) { this.r = r; this.s = s; this.vert = v; }
        void pohyb(int[][] m) {
            if (vert) { if (r+sm >= 11 || r+sm < 0 || m[r+sm][s] == 1) sm *= -1; else r += sm; }
            else { if (s+sm >= 20 || s+sm < 0 || m[r][s+sm] == 1) sm *= -1; else s += sm; }
        }
        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(255, 105, 180));
            g.fillOval(s*sV+8, r*vV+8, sV-16, vV-16);
        }
    }

    class Projektil {
        int r, s, smer;
        Projektil(int r, int s, int sm) { this.r = r; this.s = s; this.smer = sm; }
        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(Color.RED);
            g.fillOval(s*sV+sV/3, r*vV+vV/3, sV/3, vV/3);
        }
    }

    public static void main(String[] args) { new Hra(); }
}