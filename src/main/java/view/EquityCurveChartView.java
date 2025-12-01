package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Simple Swing panel that plots an equity curve.
 * X-axis = time (day index), Y-axis = equity value.
 */
public class EquityCurveChartView extends JPanel {

    private final List<Double> equityCurve;

    // Margins around the plotting area
    private static final int LEFT_MARGIN   = 70;
    private static final int RIGHT_MARGIN  = 40;
    private static final int TOP_MARGIN    = 40;
    private static final int BOTTOM_MARGIN = 70;

    public EquityCurveChartView(List<Double> equityCurve) {
        this.equityCurve = equityCurve;
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    /**
     * Convenience method: show the chart in its own dialog.
     */
    public static void showEquityCurve(List<Double> equityCurve) {
        EquityCurveChartView panel = new EquityCurveChartView(equityCurve);

        JDialog dialog = new JDialog((Frame) null, "Equity Curve", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (equityCurve == null || equityCurve.size() < 2) {
            drawNoDataMessage(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width  = getWidth();
        int height = getHeight();

        int plotX0 = LEFT_MARGIN;
        int plotY0 = TOP_MARGIN;
        int plotX1 = width - RIGHT_MARGIN;
        int plotY1 = height - BOTTOM_MARGIN;

        // Axes
        g2.setColor(Color.BLACK);
        // X-axis
        g2.drawLine(plotX0, plotY1, plotX1, plotY1);
        // Y-axis
        g2.drawLine(plotX0, plotY0, plotX0, plotY1);

        // Find min/max equity
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double v : equityCurve) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        if (min == max) {
            // avoid division by zero; give min/max a bit of range
            min *= 0.95;
            max *= 1.05;
        }

        // ----- Y-axis ticks (equity values) -----
        int yTickCount = 5;
        g2.setFont(g2.getFont().deriveFont(11f));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < yTickCount; i++) {
            double t = (double) i / (yTickCount - 1); // 0 .. 1
            double value = min + (max - min) * (1.0 - t); // top = max

            int y = (int) (plotY0 + (plotY1 - plotY0) * t);

            // tick line
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(plotX0 - 4, y, plotX1, y);

            // label
            g2.setColor(Color.BLACK);
            String label = String.format("$%.0f", value);
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, plotX0 - 8 - labelWidth, y + fm.getAscent() / 2 - 2);
        }

        // ----- X-axis ticks (time / day index) -----
        int n = equityCurve.size();
        int xTickCount = Math.min(6, n); // up to 6 ticks

        for (int i = 0; i < xTickCount; i++) {
            int index = (int) Math.round((double) i * (n - 1) / (xTickCount - 1));
            double t = (double) index / (n - 1);

            int x = (int) (plotX0 + (plotX1 - plotX0) * t);

            // tick
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, plotY1 + 4, x, plotY0);

            // label like "Day 0", "Day 5", ...
            g2.setColor(Color.BLACK);
            String label = "Day " + index;
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, x - labelWidth / 2, plotY1 + fm.getAscent() + 6);
        }

        // ----- Plot equity curve -----
        g2.setColor(new Color(0, 70, 160));

        for (int i = 0; i < n - 1; i++) {
            double v1 = equityCurve.get(i);
            double v2 = equityCurve.get(i + 1);

            double t1 = (double) i / (n - 1);
            double t2 = (double) (i + 1) / (n - 1);

            int x1 = (int) (plotX0 + (plotX1 - plotX0) * t1);
            int x2 = (int) (plotX0 + (plotX1 - plotX0) * t2);

            int y1 = valueToY(v1, min, max, plotY0, plotY1);
            int y2 = valueToY(v2, min, max, plotY0, plotY1);

            g2.drawLine(x1, y1, x2, y2);
        }

        // Axis titles
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        String xLabel = "Time (trading days)";
        int xLabelWidth = g2.getFontMetrics().stringWidth(xLabel);
        g2.drawString(xLabel,
                plotX0 + (plotX1 - plotX0 - xLabelWidth) / 2,
                height - 25);

        String yLabel = "Equity";
        drawRotatedText(g2, yLabel, 15, plotY0 + (plotY1 - plotY0) / 2);

        g2.dispose();
    }

    private int valueToY(double v, double min, double max, int plotY0, int plotY1) {
        double norm = (v - min) / (max - min); // 0 .. 1
        return (int) (plotY1 - norm * (plotY1 - plotY0));
    }

    private void drawNoDataMessage(Graphics g) {
        String msg = "No equity curve data available.";
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(14f));
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2;
        g.drawString(msg, x, y);
    }

    /**
     * Draws text rotated 90 degrees counter-clockwise (for Y-axis label).
     */
    private void drawRotatedText(Graphics2D g2, String text, int x, int y) {
        g2 = (Graphics2D) g2.create();
        g2.translate(x, y);
        g2.rotate(-Math.PI / 2);
        g2.drawString(text, 0, 0);
        g2.dispose();
    }
}