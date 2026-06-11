import javax.swing.JFrame;
import java.io.IOException;

public class GUI {
    public static void main(String[] args) throws IOException {
        //initializing GUI
        JFrame frame = new JFrame("Equations"); //not sure what to name it
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 580);
        frame.setLocationRelativeTo(null);

        DisplayPanel panel = new DisplayPanel();
        frame.add(panel);

        frame.setVisible(true);



    }
}
