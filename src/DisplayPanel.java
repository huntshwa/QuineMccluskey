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
    private JScrollBar horzScroller;
    private JScrollBar vertScroller;

    public DisplayPanel() {

        inputSlider = new JSlider(1, 20, 4); //can't have less than 1 input, inefficient with more than 20, letter transformation stops at 26, four is common
        outputSlider = new JSlider(1, 20, 1); //need at least one output, prob don't need more than 20 (can be changed), one is common

        inputSlider.setPreferredSize(new Dimension(200, 25));
        outputSlider.setPreferredSize(new Dimension(200, 25));

        int outputVal = outputSlider.getValue();
        int inputVal = inputSlider.getValue();

        // create the buttons
        JButton updateButton = new JButton("Update Table");
        JButton saveButton = new JButton("Save");
        JButton goButton = new JButton("Go!");
        DefaultTableModel model = new DefaultTableModel((int) Math.pow(2, inputVal), inputVal + outputVal); //based on sliders
        table = new JTable(model);

        // create the scroller
        horzScroller = new JScrollBar(JScrollBar.HORIZONTAL);
        vertScroller = new JScrollBar(JScrollBar.VERTICAL);
        horzScroller.setPreferredSize(new Dimension(100, 10));
        vertScroller.setPreferredSize(new Dimension(10, 100));
//        vertScroller.

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

        JLabel inputLabel = new JLabel("Inputs");
        JLabel outputLabel = new JLabel("Outputs");

        sliderPanel.add(outputSlider);

        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        outputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        outputSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        sliderPanel.add(inputLabel);
        sliderPanel.add(inputSlider);
        sliderPanel.add(outputLabel);
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
        tablePanel.add(horzScroller, BorderLayout.CENTER);
        tablePanel.add(vertScroller, BorderLayout.CENTER);

        // creating a third panel to place slider and bottom panels vertically
        // (allows two rows of UI elements to be displayed)
        JPanel combinedPanels = new JPanel();
        combinedPanels.setLayout(new GridLayout(2, 2));
        combinedPanels.add(sliderPanel, BorderLayout.NORTH);
        combinedPanels.add(tablePanel, BorderLayout.WEST);
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
                model.setColumnCount(inputSlider.getValue() + outputSlider.getValue());
                model.setRowCount((int) Math.pow(2, inputSlider.getValue()));
                table.doLayout();
            } else if (text.equals("Save")) {
                System.out.println();
            } else if (text.equals("Go!")) {
                System.out.println();
            }
        }
    }

//    @Override
//    public void adjustTable(AdjustmentEvent e) {
//
//    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
