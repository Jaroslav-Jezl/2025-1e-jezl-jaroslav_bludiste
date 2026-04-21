import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Hra extends JFrame {
    Mapa mojeMapa = new Mapa();
    int aktualniLevel = 1;
    int hracR = 1, hracS = 1;
    int tikHry = 0;

    ArrayList<Projektil> strely = new ArrayList<>();
    ArrayList<Ryba> ryby = new ArrayList<>();

    public Hra() {
        setTitle("MAZE Jezl Jaroslav");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel platno = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                super.paintComponent(g2);
                mojeMapa.vykresli(g2, getWidth(), getHeight(), aktualniLevel);
                if (aktualniLevel == 2) {
                    for (Ryba r : ryby) r.vykresli(g2, mojeMapa.sirkaPole, mojeMapa.vyskaPole);
                }
                if (aktualniLevel == 3) {
                    for (Projektil p : strely) p.vykresli(g2, mojeMapa.sirkaPole, mojeMapa.vyskaPole);
                }
                g2.setColor(new Color(0, 255, 255, 200));
                g2.fillRect(hracS * mojeMapa.sirkaPole + 8, hracR * mojeMapa.vyskaPole + 8, mojeMapa.sirkaPole - 16, mojeMapa.vyskaPole - 16);
            }
        };
        add(platno);

        new Timer(3000, e -> {
            if (aktualniLevel == 3) {
                strely.clear();
                strely.add(new Projektil(1, 16, -1));
                strely.add(new Projektil(5, 0, 1));
                strely.add(new Projektil(9, 2, 1));
                strely.add(new Projektil(9, 14, 1));
            }
        }).start();

        new Timer(50, e -> {
            tikHry++;
            if (aktualniLevel == 2 && tikHry % 4 == 0) {
                for (Ryba r : ryby) {
                    r.pohyb(mojeMapa.rozlozeni);
                    if (r.r == hracR && r.s == hracS) { hracR = 1; hracS = 1; }
                }
            }
            if (aktualniLevel == 3 && tikHry % 2 == 0) {
                for (int i = strely.size() - 1; i >= 0; i--) {
                    Projektil p = strely.get(i);
                    p.s += p.smer;
                    p.vzdalenost++;
                    if (p.r == hracR && p.s == hracS) { hracR = 1; hracS = 1; }
                    if (p.vzdalenost >= 4 || p.s < 0 || p.s >= 20 || mojeMapa.rozlozeni[p.r][p.s] == 1) {
                        strely.remove(i);
                    }
                }
            }
            repaint();
        }).start();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int r = hracR, s = hracS;
                if (e.getKeyCode() == KeyEvent.VK_UP) r--;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) r++;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) s--;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) s++;

                if (r >= 0 && r < 11 && s >= 0 && s < 20 && mojeMapa.rozlozeni[r][s] != 1) {
                    hracR = r; hracS = s;
                    if (mojeMapa.rozlozeni[r][s] == 2) {
                        if (aktualniLevel < 3) prepniLevel(aktualniLevel + 1);
                        else {
                            // TADY JE TA OPRAVA BEZ IKONKY
                            JOptionPane.showMessageDialog(null, "Hra ukoncena. Dobra prace!", "Konec", JOptionPane.PLAIN_MESSAGE);
                            System.exit(0);
                        }
                    }
                }
                repaint();
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    void prepniLevel(int lvl) {
        aktualniLevel = lvl;
        ryby.clear();
        strely.clear();
        if (lvl == 1) mojeMapa.rozlozeni = mojeMapa.level1;
        else if (lvl == 2) {
            mojeMapa.rozlozeni = mojeMapa.level2;
            ryby.add(new Ryba(3, 5));
            ryby.add(new Ryba(5, 10));
        } else if (lvl == 3) mojeMapa.rozlozeni = mojeMapa.level3;
        hracR = 1; hracS = 1;
    }

    class Ryba {
        int r, s, sm = 1;
        Ryba(int r, int s) { this.r = r; this.s = s; }
        void pohyb(int[][] m) {
            if (s+sm >= 20 || s+sm < 0 || m[r][s+sm] == 1) sm *= -1; else s += sm;
        }
        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(221, 160, 221));
            g.fillOval(s*sV+12, r*vV+12, sV-24, vV-24);
        }
    }

    class Projektil {
        int r, s, smer, vzdalenost = 0;
        Projektil(int r, int s, int sm) { this.r = r; this.s = s; this.smer = sm; }
        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(255, 215, 0));
            int vel = sV / 2;
            g.fillOval(s*sV + (sV-vel)/2, r*vV + (vV-vel)/2, vel, vel);
        }
    }

    public static void main(String[] args) { new Hra(); }
}