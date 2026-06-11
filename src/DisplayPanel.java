import javax.swing.table.DefaultTableModel;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class DisplayPanel extends JPanel implements ActionListener, ChangeListener {
    private static JTable table;
    private JSlider outputSlider;
    private JSlider inputSlider;
    private JTextArea outputText;
    private static ArrayList<String> outputs;

    public DisplayPanel() throws IOException {

        //create sliders
        inputSlider = new JSlider(1, 10, 4); //can't have less than 1 input, inefficient with more than 15, letter transformation stops at 26, four is common
        outputSlider = new JSlider(1, 20, 1); //need at least one output, prob don't need more than 20 (can be changed), one is common

        inputSlider.setMinorTickSpacing(1);
        inputSlider.setMajorTickSpacing(5);
        outputSlider.setMinorTickSpacing(1);
        outputSlider.setMajorTickSpacing(5);
        inputSlider.setPaintTicks(true);
        inputSlider.setPaintLabels(true);
        outputSlider.setPaintTicks(true);
        outputSlider.setPaintLabels(true);

        int outputVal = outputSlider.getValue();
        int inputVal = inputSlider.getValue();

        //creating labels for sliders
        JLabel inputLabel = new JLabel("Inputs");
        JLabel outputLabel = new JLabel("Outputs");

        //create the buttons
        JButton updateButton = new JButton("Update Table");
        JButton saveButton = new JButton("Save");
        JButton goButton = new JButton("Go!");

        //create table
        DefaultTableModel model = new DefaultTableModel((int) Math.pow(2, inputVal), inputVal + outputVal); //based on sliders
        table = new JTable(model);

        doInputs(model, inputSlider);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(200, 580));

        controlPanel.add(inputLabel);
        controlPanel.add(inputSlider);
        controlPanel.add(Box.createVerticalStrut(15));

        controlPanel.add(outputLabel);
        controlPanel.add(outputSlider);
        controlPanel.add(Box.createVerticalStrut(15));

        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        outputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        outputSlider.setAlignmentX(Component.CENTER_ALIGNMENT);


        //add bottom components to the panel, in left-to-right order
        controlPanel.add(updateButton);
        controlPanel.add(saveButton);
        controlPanel.add(goButton);

        //create a panel for the table
        JScrollPane tableScrollPlane = new JScrollPane(table);

        //formatting panel for throwing everything together
        JPanel combinedPanels = new JPanel();
        combinedPanels.setLayout(new BorderLayout());

        combinedPanels.add(controlPanel, BorderLayout.EAST);
        combinedPanels.add(tableScrollPlane, BorderLayout.WEST);

        add(combinedPanels, BorderLayout.SOUTH);

        FileWriter fw = new FileWriter("src/WriteTable.csv", false);

        //adding buttons to action listener
        goButton.addActionListener(this);
        updateButton.addActionListener(this);
        saveButton.addActionListener(this);

        //setting up slider to use ChangeListener interface and stateChanged method
        inputSlider.addChangeListener(this);
        outputSlider.addChangeListener(this);

    }

    public int getNumInputs() {
        return inputSlider.getValue();
    }

    public static void updateOutputs(String output) {
        outputs.add(output);
    }

    public static ArrayList<String> getOutputs() {
        return outputs;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawString("Truth Table", 50, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            String text = button.getText();

            if (text.equals("Update Table")) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                clearTable(table, inputSlider, outputSlider);

                model.setColumnCount(inputSlider.getValue() + outputSlider.getValue());
                model.setRowCount((int) Math.pow(2, inputSlider.getValue()));
                table.doLayout();

                doInputs(model, inputSlider);

            } else if (text.equals("Save")) {
                try {
                    writeTable();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Saved");
            } else if (text.equals("Go!")) {
                Algorithm.setNumInputs(inputSlider.getValue());
                try {
                    Algorithm.runAlgorithm();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

//                JPanel outputScreen = new JPanel();
//                outputScreen.add(outputText);

            }
        }
    }

//    public void displayOutput( )

    public static void doInputs(DefaultTableModel model, JSlider slider) {
        for (int row = 0; row < model.getRowCount(); row++) {
            String binary = Integer.toBinaryString(row);

            while (binary.length() < slider.getValue()) {
                binary = "0" + binary;
            }

            for (int col = slider.getValue() - 1; col >= 0; col--) {
                if (col >= binary.length()) {
                    model.setValueAt(0, row, col);
                } else {
                    model.setValueAt(Integer.parseInt(String.valueOf(binary.charAt(col))), row, col);
                }
            }
        }
    }

    public static void clearTable(JTable table, JSlider slider1, JSlider slider2) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnCount(0);
        model.setRowCount(0);
        table.doLayout();
    }

    public static void writeTable() throws IOException {
        FileWriter fw = new FileWriter("src/WriteTable.csv", false);
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/WriteTable.csv"));
        DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        if (j == model.getColumnCount() - 1) {
                            writer.write((String) value);
                        } else {
                            writer.write(value + ",");
                        }
                    } else {
                        writer.write("0");
                    }
                }
                writer.newLine();
            }
        writer.close();
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}