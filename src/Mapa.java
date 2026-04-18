import java.awt.*;
import java.awt.geom.Point2D;

public class Mapa {
    // 1=zeď, 0=cesta, 2=cíl, 3=portál, 4=past, 5=láva, 6=voják

    public int[][] level1 = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1},
            {1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,1,0,1,0,1},
            {1,0,1,0,0,0,0,0,1,0,1,0,0,0,0,1,0,1,0,1},
            {1,0,1,1,1,1,1,1,1,0,1,0,1,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // LEVEL 2: Zamotaná cesta, kde ryby křižují hlavní průchody
    public int[][] level2 = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
            {1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,0,1,0,0,1}, // Tady nahoře křižuje první ryba
            {1,0,1,1,1,1,1,0,1,1,1,0,1,0,0,0,1,0,1,1},
            {1,0,1,0,0,0,0,0,1,0,0,0,1,1,1,1,1,0,0,1}, // Prostřední chodba pro druhou rybu
            {1,0,1,0,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,1,0,1},
            {1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public int[][] level3 = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,1,0,0,0,0,0,1,0,0,0,6,0,0,0,0,1},
            {1,5,5,0,1,0,1,1,1,0,1,0,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,5,5,1,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,1,0,1,0,1,0,1},
            {1,0,1,6,0,0,0,0,1,3,0,0,0,1,0,1,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,0,1,0,1,1,1,1,1},
            {1,0,0,0,5,5,1,0,0,0,6,0,0,1,0,0,0,0,0,1},
            {1,1,1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public int[][] rozlozeni = level1;
    public int velikostPole;
    public boolean lavaAktivni = false;
    private float animaceLavy = 0;

    public void vykresli(Graphics2D g, int sirka, int vyska, int level) {
        // Fix: Vždy počítáme s 20 sloupci a 12 řádky, aby byla mapa stabilní
        velikostPole = Math.min(sirka / 20, vyska / 12);
        animaceLavy += 0.1f;

        for (int r = 0; r < rozlozeni.length; r++) {
            for (int s = 0; s < rozlozeni[r].length; s++) {
                int b = rozlozeni[r][s];
                int x = s * velikostPole;
                int y = r * velikostPole;

                // Podlaha
                g.setColor(new Color(15, 15, 25));
                g.fillRect(x, y, velikostPole, velikostPole);

                if (b == 1) { // ZEĎ
                    g.setColor(level == 3 ? new Color(90, 30, 30) : (level == 2 ? new Color(40, 50, 100) : new Color(50, 70, 50)));
                    g.fillRoundRect(x, y, velikostPole, velikostPole, 10, 10);
                    g.setColor(new Color(255, 255, 255, 40));
                    g.drawRoundRect(x+2, y+2, velikostPole-4, velikostPole-4, 5, 5);
                } else if (b == 5) { // LÁVA
                    g.setColor(lavaAktivni ? new Color(230, 80, 0) : new Color(60, 40, 35));
                    g.fillRect(x, y, velikostPole, velikostPole);
                } else if (b == 3) { // PORTÁL
                    g.setColor(new Color(190, 60, 255));
                    g.fillOval(x+4, y+4, velikostPole-8, velikostPole-8);
                } else if (b == 2) { // CÍL
                    g.setColor(Color.WHITE);
                    g.fillOval(x+10, y+10, velikostPole-20, velikostPole-20);
                } else if (b == 4) { // PAST
                    g.setColor(Color.RED);
                    g.fillRect(x+12, y+12, velikostPole-24, velikostPole-24);
                } else if (b == 6) { // VOJÁK
                    g.setColor(Color.DARK_GRAY);
                    g.fillRoundRect(x+6, y+6, velikostPole-12, velikostPole-12, 5, 5);
                }
            }
        }
    }

    public void vykresliMlhu(Graphics2D g, int hracX, int hracY, int sirka, int vyska) {
        if (velikostPole <= 0) return;
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point2D.Double(hracX + velikostPole/2.0, hracY + velikostPole/2.0),
                (float)(velikostPole * 4.2),
                new float[]{0.0f, 0.75f, 1.0f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 130), new Color(0, 0, 0, 255)}
        );
        g.setPaint(rgp);
        g.fillRect(0, 0, sirka, vyska);
    }
}