import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JFrame;

public class snakeJFrame extends Applet{
    private static snakeCanvas c;
    public static void main(String[] args) {
        c = new snakeCanvas();
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(640,480));
        frame.add(c);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public void init()
    {
        c = new snakeCanvas();
        c.setVisible(true);
        c.setFocusable(true);
        this.add(c);
        this.setVisible(true);
        this.setSize(new Dimension(640,480));
    }

    public void paint(Graphics g)
    {
        this.setSize(new Dimension(640,480));
    }
}