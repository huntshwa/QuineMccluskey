import javax.swing.table.DefaultTableModel;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


public class DisplayPanel extends JPanel implements ActionListener, ChangeListener {
    private JTable table;
    private JSlider outputSlider;
    private JSlider inputSlider;

    public DisplayPanel() {

        inputSlider = new JSlider(1, 20, 4); //can't have less than 1 input, inefficient with more than 20, letter transformation stops at 26, four is common
        outputSlider = new JSlider(1, 20, 1); //need at least one output, prob don't need more than 20 (can be changed), one is common
        int outputVal = outputSlider.getValue();
        int inputVal = inputSlider.getValue();

        // create the buttons
        JButton updateButton = new JButton("Update Table");
        JButton saveButton = new JButton("Save");
        JButton goButton = new JButton("Go!");

        table = new JTable((int) Math.pow(2, inputVal), inputVal + outputVal);

        // create sliders and adjust settings
        inputSlider.setMinorTickSpacing(1);
        inputSlider.setMajorTickSpacing(5);
        outputSlider.setMinorTickSpacing(1);
        outputSlider.setMajorTickSpacing(5);
        inputSlider.setPaintTicks(true);
        inputSlider.setPaintLabels(true);
        outputSlider.setPaintTicks(true);
        outputSlider.setPaintLabels(true);

        // create a panel for organizing the label and slider
        JPanel sliderPanel = new JPanel();

        sliderPanel.add(inputSlider);
        sliderPanel.add(outputSlider);

        // create a panel for organizing the components at the bottom
        JPanel buttonPanel = new JPanel(); // a "panel" is not visible

        // add bottom components to the panel, in left-to-right order
        buttonPanel.add(updateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(goButton);

        // create a panel for the table
        JPanel tablePanel = new JPanel();

        tablePanel.add(table);

        // creating a third panel to place slider and bottom panels vertically
        // (allows two rows of UI elements to be displayed)
        JPanel combinedPanels = new JPanel();
        combinedPanels.setLayout(new GridLayout(2, 2));
        combinedPanels.add(sliderPanel, BorderLayout.NORTH);
        combinedPanels.add(tablePanel, BorderLayout.CENTER);
        combinedPanels.add(buttonPanel, BorderLayout.EAST);

        add(combinedPanels, BorderLayout.SOUTH);

        // --- SETTING UP EVENT HANDLING ----
        //setting up buttons to use ActionListener interface and actionPerformed method
        goButton.addActionListener(this);
        updateButton.addActionListener(this);
        saveButton.addActionListener(this);

        //setting up slider to use ChangeListener interface and stateChanged method
        inputSlider.addChangeListener(this);
        outputSlider.addChangeListener(this);
    }

    //gonna try to create a truth table
    public void createTable(int rows, int columns){ //maybe this could just be like 2 values inputted (length x width) rather than a 2d array
       //rows = 2 ^ input slider listener;
        //columns = slider listener;
        DefaultTableModel rowCol = new DefaultTableModel(rows, columns);
        JTable table = new JTable(rowCol);
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
                table = new JTable((int) Math.pow(2, inputSlider.getValue()), inputSlider.getValue() + outputSlider.getValue());
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
