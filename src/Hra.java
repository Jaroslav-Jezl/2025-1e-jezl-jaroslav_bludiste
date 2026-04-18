import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Hra extends JFrame {
    Mapa mojeMapa = new Mapa();
    int aktualniLevel = 1;
    int hracRadek = 1, hracSloupec = 1;
    ArrayList<Projektil> strely = new ArrayList<>();
    ArrayList<Ryba> ryby = new ArrayList<>();

    public Hra() {
        setTitle("MAZE MASTER: FOG OF WAR");
        setSize(1200, 800);
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

                // HRÁČ
                int px = hracSloupec * mojeMapa.velikostPole;
                int py = hracRadek * mojeMapa.velikostPole;

                // Nepřátelé (kreslíme je pod mlhu, aby nebyli vidět v dálce)
                if (aktualniLevel == 2) for (Ryba r : ryby) r.vykresli(g2, mojeMapa.velikostPole);
                if (aktualniLevel == 3) for (Projektil p : strely) p.vykresli(g2, mojeMapa.velikostPole);

                // APLIKACE MLHY (jen v Levelu 3)
                if (aktualniLevel == 3) {
                    mojeMapa.vykresliMlhu(g2, px, py, getWidth(), getHeight());
                }

                // Vykreslení hráče až NAD mlhu, aby vždy zářil
                g2.setColor(new Color(0, 255, 255));
                g2.fillRoundRect(px+6, py+6, mojeMapa.velikostPole-12, mojeMapa.velikostPole-12, 10, 10);
            }
        };
        add(platno);

        // Game Loop (Střely a Ryby)
        Timer gameLoop = new Timer(50, e -> {
            if (aktualniLevel == 3) {
                // Extrémní střelba z více směrů
                if (Math.random() < 0.18) {
                    strely.add(new Projektil(1, 3, 1));
                    strely.add(new Projektil(1, 17, -1));
                    strely.add(new Projektil(7, 16, -1));
                }
                for (int i = strely.size()-1; i >= 0; i--) {
                    Projektil p = strely.get(i);
                    p.s += p.smer;
                    if (p.r == hracRadek && p.s == hracSloupec) resetujHrace();
                    if (p.s < 0 || p.s >= 20 || mojeMapa.rozlozeni[p.r][p.s] == 1) strely.remove(i);
                }
            }
            if (aktualniLevel == 2) {
                for (Ryba r : ryby) {
                    r.aktualizuj(mojeMapa.rozlozeni);
                    if (r.r == hracRadek && r.s == hracSloupec) resetujHrace();
                }
            }
            repaint();
        });
        gameLoop.start();

        Timer lavaTimer = new Timer(900, e -> {
            if (aktualniLevel == 3) {
                mojeMapa.lavaAktivni = !mojeMapa.lavaAktivni;
                if (mojeMapa.lavaAktivni && mojeMapa.rozlozeni[hracRadek][hracSloupec] == 5) resetujHrace();
                repaint();
            }
        });
        lavaTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int kod = e.getKeyCode();
                int nr = hracRadek, ns = hracSloupec;
                if (kod == KeyEvent.VK_UP) nr--;
                else if (kod == KeyEvent.VK_DOWN) nr++;
                else if (kod == KeyEvent.VK_LEFT) ns--;
                else if (kod == KeyEvent.VK_RIGHT) ns++;

                if (nr >= 0 && nr < mojeMapa.rozlozeni.length && ns >= 0 && ns < mojeMapa.rozlozeni[0].length) {
                    int b = mojeMapa.rozlozeni[nr][ns];
                    if (b != 1) {
                        hracRadek = nr; hracSloupec = ns;
                        if (b == 5 && mojeMapa.lavaAktivni) resetujHrace();
                        if (b == 4) resetujHrace();
                        if (b == 3) { hracRadek = 8; hracSloupec = 18; }
                        if (b == 2) {
                            if (aktualniLevel < 3) prepniLevel();
                            else { JOptionPane.showMessageDialog(null, "BOŽSKÝ VÝKON! PROŠEL JSI MLHOU."); System.exit(0); }
                        }
                    }
                }
                repaint();
            }
        });
        setFocusable(true);
        setVisible(true);
    }

    void prepniLevel() {
        aktualniLevel++;
        if (aktualniLevel == 2) {
            mojeMapa.rozlozeni = mojeMapa.level2;
            ryby.add(new Ryba(3, 10, true));
            ryby.add(new Ryba(7, 5, false));
        } else if (aktualniLevel == 3) {
            mojeMapa.rozlozeni = mojeMapa.level3;
        }
        resetujHrace();
    }

    void resetujHrace() { hracRadek = 1; hracSloupec = 1; repaint(); }

    class Ryba {
        int r, s, smer = 1; boolean v;
        Ryba(int r, int s, boolean v) { this.r = r; this.s = s; this.v = v; }
        void aktualizuj(int[][] m) {
            if (v) { if (r + smer >= m.length || r + smer < 0 || m[r+smer][s] == 1) smer *= -1; else r += smer; }
            else { if (s + smer >= m[0].length || s + smer < 0 || m[r][s+smer] == 1) smer *= -1; else s += smer; }
        }
        void vykresli(Graphics2D g, int vel) {
            g.setColor(new Color(0, 200, 255));
            g.fillOval(s * vel + 8, r * vel + 8, vel - 16, vel - 16);
        }
    }

    class Projektil {
        int r, s, smer;
        Projektil(int r, int s, int sm) { this.r = r; this.s = s; this.smer = sm; }
        void vykresli(Graphics2D g, int v) {
            g.setColor(Color.YELLOW);
            g.fillOval(s * v + v/3, r * v + v/3, v/4, v/4);
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Hra()); }
}