import javax.swing.*;
import java.awt.Graphics;
import java.awt.Dimension;

public class DisplayPanel extends JPanel {

    public DisplayPanel() {

    }

    //gonna try to create a truth table
    public void createTable(Object[][] rowCol){ //maybe this could just be like 2 values inputted (length x width) rather than a 2d array
        JTable table = new JTable();

    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawString("Truth Table", 50, 30);
    }
}
