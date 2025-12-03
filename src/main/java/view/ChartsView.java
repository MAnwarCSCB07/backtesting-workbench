package view;

import interface_adapter.ViewManagerModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class ChartsView extends JPanel {
    public final String viewName = "charts";

    public ChartsView(ViewManagerModel viewManagerModel) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);
        
        JLabel title = new JLabel("Charts Page");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(title);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "Row 1", "Column 1");
        dataset.addValue(5.0, "Row 1", "Column 2");
        dataset.addValue(3.0, "Row 1", "Column 3");
        dataset.addValue(2.0, "Row 2", "Column 1");
        dataset.addValue(3.0, "Row 2", "Column 2");
        dataset.addValue(2.5, "Row 2", "Column 3");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Sample Bar Chart",
                "Category",
                "Value",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        this.add(Box.createVerticalStrut(20));
        this.add(chartPanel);
        
        this.add(Box.createVerticalStrut(20));
        
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });
        this.add(backButton);
    }
}
