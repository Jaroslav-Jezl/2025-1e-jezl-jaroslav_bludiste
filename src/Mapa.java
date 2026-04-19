import java.awt.*;
import java.awt.geom.Point2D;

public class Mapa {
    // Definice všech tří levelů
    public int[][] level1 = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
            {1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1},
            {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public int[][] level2 = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
            {1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,0,1,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,0,1,0,0,0,1,0,1,1},
            {1,0,1,0,0,0,0,0,1,0,0,0,1,1,1,1,1,0,0,1},
            {1,0,1,0,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,1,0,1},
            {1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public int[][] level3 = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,6,1,1,0,1,0,6,1,1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,5,5,0,1},
            {1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1},
            {1,6,0,0,0,0,0,0,6,0,0,0,0,0,6,0,0,0,0,1},
            {1,1,6,6,6,1,1,1,1,5,5,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,6,0,0,0,0,0,0,6,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,5,5,5,5,5,5,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public int[][] rozlozeni = level1;
    public int sirkaPole, vyskaPole;
    public boolean lavaAktivni = false;
    public boolean strelciAktivni = false;

    public void vykresli(Graphics2D g, int sirkaOkna, int vyskaOkna, int level) {
        sirkaPole = sirkaOkna / 20;
        vyskaPole = vyskaOkna / 11;

        for (int r = 0; r < rozlozeni.length; r++) {
            for (int s = 0; s < rozlozeni[r].length; s++) {
                int b = rozlozeni[r][s];
                int x = s * sirkaPole;
                int y = r * vyskaPole;

                // Pozadí čtverečku
                g.setColor(new Color(15, 15, 25));
                g.fillRect(x, y, sirkaPole, vyskaPole);

                if (b == 1) { // Zed
                    g.setColor(level == 3 ? new Color(95, 30, 30) : (level == 2 ? new Color(40, 50, 110) : new Color(50, 80, 50)));
                    g.fillRoundRect(x + 1, y + 1, sirkaPole - 2, vyskaPole - 2, 12, 12);
                } else if (b == 5) { // Láva
                    g.setColor(lavaAktivni ? new Color(240, 70, 0) : new Color(50, 30, 25));
                    g.fillRect(x, y, sirkaPole, vyskaPole);
                } else if (b == 2) { // Cíl
                    g.setColor(Color.WHITE);
                    g.fillOval(x + sirkaPole/4, y + vyskaPole/4, sirkaPole/2, sirkaPole/2);
                } else if (b == 6) { // Střelec
                    g.setColor(strelciAktivni ? new Color(255, 0, 0) : new Color(80, 80, 90));
                    g.fillRoundRect(x + 4, y + 4, sirkaPole - 8, vyskaPole - 8, 8, 8);
                }
            }
        }
    }

    public void vykresliMlhu(Graphics2D g, int hracS, int hracR, int sirkaOkna, int vyskaOkna) {
        int px = hracS * sirkaPole + sirkaPole/2;
        int py = hracR * vyskaPole + vyskaPole/2;
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point2D.Double(px, py), (float)(sirkaPole * 4.5),
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 150), new Color(0, 0, 0, 255)}
        );
        g.setPaint(rgp);
        g.fillRect(0, 0, sirkaOkna, vyskaOkna);
    }
}