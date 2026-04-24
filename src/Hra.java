import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Hra extends JFrame {
    Mapa plan = new Mapa();
    int cislo_lvl = 1;
    int hracR = 1, hracS = 1;
    int casovac = 0;

    ArrayList<Koule> naboje = new ArrayList<>();
    ArrayList<Ryba> nepratele = new ArrayList<>();

    public Hra() {
        setTitle("MAZE - Jaroslav Jezl");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel plocha = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                super.paintComponent(g2);

              
                plan.vykresli(g2, getWidth(), getHeight(), cislo_lvl);
                if (cislo_lvl == 2) {
                    for (Ryba r : nepratele) r.vykresli(g2, plan.sirka_ctverce, plan.vyska_ctverce);
                }
                if (cislo_lvl == 3) {
                    for (Koule k : naboje) k.vykresli(g2, plan.sirka_ctverce, plan.vyska_ctverce);
                }

                // hrac - svetle modry
                g2.setColor(new Color(50, 200, 255));
                g2.fillRect(hracS * plan.sirka_ctverce + 7, hracR * plan.vyska_ctverce + 7, plan.sirka_ctverce - 14, plan.vyska_ctverce - 14);
            }
        };

        add(plocha);


        new Timer(3000, e -> {
            if (cislo_lvl == 3) {
                naboje.clear(); // stary pryc
                naboje.add(new Koule(1, 16, -1));
                naboje.add(new Koule(5, 0, 1));
                naboje.add(new Koule(9, 2, 1));
                naboje.add(new Koule(9, 14, 1));
            }
        }).start();


        new Timer(50, e -> {
            casovac++;

            if (cislo_lvl == 2 && casovac % 4 == 0) {
                for (Ryba r : nepratele) {
                    r.pohyb(plan.rozlozeni);
                    if (r.r == hracR && r.s == hracS) { hracR = 1; hracS = 1; } // smrt
                }
            }

            if (cislo_lvl == 3 && casovac % 2 == 0) {
                for (int i = naboje.size() - 1; i >= 0; i--) {
                    Koule k = naboje.get(i);
                    k.s += k.smer;
                    k.let = k.let + 1;
                    if (k.r == hracR && k.s == hracS) { hracR = 1; hracS = 1; }
                    if (k.let >= 4 || k.s < 0 || k.s >= 20 || plan.rozlozeni[k.r][k.s] == 1) {
                        naboje.remove(i);
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

              
                if (r >= 0 && r < 11 && s >= 0 && s < 20 && plan.rozlozeni[r][s] != 1) {
                    hracR = r; hracS = s;
                    if (plan.rozlozeni[r][s] == 2) { // cil
                        if (cislo_lvl < 3) dalsiLvl(cislo_lvl + 1);
                        else {
                            JOptionPane.showMessageDialog(null, "Hotovo Vyhral jsi.", "Konec hry", JOptionPane.PLAIN_MESSAGE);
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

    void dalsiLvl(int l) {
        cislo_lvl = l;
        nepratele.clear();
        naboje.clear();
        if (l == 1) plan.rozlozeni = plan.level1;
        else if (l == 2) {
            plan.rozlozeni = plan.level2;
            nepratele.add(new Ryba(3, 5));
            nepratele.add(new Ryba(5, 10));
        } else if (l == 3) plan.rozlozeni = plan.level3;
        hracR = 1; hracS = 1;
    }


    class Ryba {
        int r, s, strana = 1;
        Ryba(int r, int s) { this.r = r; this.s = s; }
        void pohyb(int[][] m) {
            if (s+strana >= 20 || s+strana < 0 || m[r][s+strana] == 1) strana *= -1;
            else s += strana;
        }
        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(200, 150, 200));
            g.fillOval(s*sV+11, r*vV+11, sV-22, vV-22);
        }
    }

  
    class Koule {
        int r, s, smer, let = 0;
        Koule(int r, int s, int sm) { this.r = r; this.s = s; this.smer = sm; }
        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(250, 200, 50));
            g.fillOval(s*sV + sV/4, r*vV + vV/4, sV/2, vV/2);
        }
    }

    public static void main(String[] args) {
        new Hra();
    }
}
