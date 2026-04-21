import java.awt.*;


public class Hrac {
    public int x = 0;
    public int y = 0;
    public int velikost = 40;


    public void vykresli(Graphics g) {


        g.setColor(new Color(255, 200, 0)); // Zlatá barva
        int m = velikost / 6;
        g.fillRoundRect(x + m, y + m, velikost - 2*m, velikost - 2*m, 10, 10);




        g.setColor(Color.BLACK);
        g.fillOval(x + m + 5, y + m + 5, 6, 6);
        g.fillOval(x + velikost - m - 11, y + m + 5, 6, 6);
    }
}
