package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class ChartsView extends JPanel {
    public final String viewName = "charts";

    public ChartsView() {
        JLabel title = new JLabel("Charts Page");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        this.add(chartPanel);
    }
}
