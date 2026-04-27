import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class Hra extends JFrame {

    Mapa plan = new Mapa();
    int cislo_lvl = 1;


    int hracR = 1;
    int hracS = 1;


    int casovac = 0;


    ArrayList<Koule> naboje = new ArrayList<>();
    ArrayList<Ryba> nepratele = new ArrayList<>();

    public Hra() {

        setTitle("MAZE - Jaroslav Jezl");
        setSize(1020, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel plocha = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                super.paintComponent(g2);


                plan.vykresli(g2, getWidth(), getHeight(), cislo_lvl);


                if (cislo_lvl == 2) {
                    for (Ryba r : nepratele) {
                        r.vykresli(g2, plan.sirka_ctverce, plan.vyska_ctverce);
                    }
                }

                if (cislo_lvl == 3) {
                    for (Koule k : naboje) {
                        k.vykresli(g2, plan.sirka_ctverce, plan.vyska_ctverce);
                    }
                }


                g2.setColor(new Color(0, 153, 255));
                int offset = 8; 
                g2.fillRoundRect(hracS * plan.sirka_ctverce + offset,
                        hracR * plan.vyska_ctverce + offset,
                        plan.sirka_ctverce - (offset * 2),
                        plan.vyska_ctverce - (offset * 2),
                        10, 10); 
            }
        };

        add(plocha);


        new Timer(3000, e -> {
            if (cislo_lvl == 3) {
                naboje.clear();

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

                    if (r.r == hracR && r.s == hracS) restartujPozici();
                }
            }


            if (cislo_lvl == 3 && casovac % 2 == 0) {
                for (int i = naboje.size() - 1; i >= 0; i--) {
                    Koule k = naboje.get(i);
                    k.s += k.smer;
                    k.let++;

                    if (k.r == hracR && k.s == hracS) restartujPozici();
                    if (k.let >= 5 || k.s < 0 || k.s >= 20 || plan.rozlozeni[k.r][k.s] == 1) {
                        naboje.remove(i);
                    }
                }
            }
            repaint();
        }).start();


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int r = hracR;
                int s = hracS;


                if (e.getKeyCode() == KeyEvent.VK_UP) r--;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) r++;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) s--;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) s++;


                if (r >= 0 && r < 11 && s >= 0 && s < 20 && plan.rozlozeni[r][s] != 1) {
                    hracR = r;
                    hracS = s;


                    if (plan.rozlozeni[r][s] == 2) {
                        if (cislo_lvl < 3) {
                            dalsiLvl(cislo_lvl + 1);
                        } else {
                            JOptionPane.showMessageDialog(null, "Dobrá Práce! Prošel jsi celým bludištěm.", "Vítězství", JOptionPane.INFORMATION_MESSAGE);
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

    private void restartujPozici() {
        hracR = 1;
        hracS = 1;
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
        } else if (l == 3) {
            plan.rozlozeni = plan.level3;
        }

        restartujPozici();
    }



    class Ryba {
        int r, s, strana = 1;
        Ryba(int r, int s) { this.r = r; this.s = s; }

        void pohyb(int[][] m) {

            if (s + strana >= 20 || s + strana < 0 || m[r][s + strana] == 1) {
                strana *= -1;
            } else {
                s += strana;
            }
        }

        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(255, 102, 102));
            g.fillOval(s * sV + 10, r * vV + 10, sV - 20, vV - 20);
        }
    }

    class Koule {
        int r, s, smer, let = 0;
        Koule(int r, int s, int sm) { this.r = r; this.s = s; this.smer = sm; }

        void vykresli(Graphics2D g, int sV, int vV) {
            g.setColor(new Color(255, 255, 0));
            g.fillOval(s * sV + sV/4, r * vV + vV/4, sV/2, vV/2);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new Hra());
    }
}
