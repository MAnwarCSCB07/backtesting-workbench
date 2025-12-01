package view;

import interface_adapter.run_backtest.RunBacktestController;
import interface_adapter.run_backtest.RunBacktestState;
import interface_adapter.run_backtest.RunBacktestViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

public class RunBacktestView extends JPanel implements PropertyChangeListener {

    private RunBacktestController controller;
    private final RunBacktestViewModel viewModel;

    // Inputs
    private final JTextField projectIdField   = new JTextField(15);
    private final JTextField tickerField      = new JTextField(10);
    private final JTextField capitalField     = new JTextField(10);
    private final JTextField startDateField   = new JTextField(10);
    private final JTextField endDateField     = new JTextField(10);

    // Actions
    private final JButton runButton = new JButton("Run Backtest");

    // Outputs (inside results panel)
    private final JLabel statusLabel      = new JLabel();
    private final JLabel finalValueLabel  = new JLabel();
    private final JLabel drawdownLabel    = new JLabel();
    private final JLabel returnLabel      = new JLabel();

    private final DecimalFormat moneyFormat   = new DecimalFormat("#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("0.00%");

    public RunBacktestView(RunBacktestViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // =========================================================================
        // SETTINGS WRAPPER (keeps form centered)
        // =========================================================================
        JPanel settingsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        settingsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = buildInputPanel();
        settingsWrapper.add(inputPanel);

        add(settingsWrapper);

        // =========================================================================
        // RUN BUTTON
        // =========================================================================
        add(Box.createRigidArea(new Dimension(0, 15)));
        runButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(runButton);

        runButton.addActionListener(e -> {
            if (controller == null) return;

            controller.runBacktest(
                    projectIdField.getText().trim(),
                    tickerField.getText().trim(),
                    capitalField.getText().trim(),
                    startDateField.getText().trim(),
                    endDateField.getText().trim()
            );
        });

        // =========================================================================
        // RESULTS WRAPPER + PANEL (centered)
        // =========================================================================
        add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel resultsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel resultsPanel = buildResultsPanel();
        resultsWrapper.add(resultsPanel);

        add(resultsWrapper);
    }

    /**
     * Builds the "Backtest Settings" input form using GridBagLayout.
     */
    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Backtest Settings"));
        panel.setPreferredSize(new Dimension(450, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.LINE_START;

        int row = 0;

        // Project ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Project ID:"), gbc);
        gbc.gridx = 1;
        projectIdField.setText("demo-project");
        panel.add(projectIdField, gbc);
        row++;

        // Ticker
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ticker:"), gbc);
        gbc.gridx = 1;
        tickerField.setText("AAPL");
        panel.add(tickerField, gbc);
        row++;

        // Capital
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Initial Capital ($):"), gbc);
        gbc.gridx = 1;
        capitalField.setText("10000");
        panel.add(capitalField, gbc);
        row++;

        // Start date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);
        row++;

        // End date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(endDateField, gbc);

        return panel;
    }

    /**
     * Builds the "Backtest Results" panel that shows status & metrics.
     */
    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Backtest Results"));
        panel.setPreferredSize(new Dimension(450, 140));

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        finalValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        drawdownLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        returnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(finalValueLabel);
        panel.add(drawdownLabel);
        panel.add(returnLabel);

        return panel;
    }

    // Called by AppBuilder
    public void setRunBacktestController(RunBacktestController controller) {
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        RunBacktestState state = viewModel.getState();

        // Status message (success / errors)
        statusLabel.setText(state.getStatusMessage());

        // Final value
        if (state.getFinalValue() != null) {
            finalValueLabel.setText("Final Value: $" + moneyFormat.format(state.getFinalValue()));
        } else {
            finalValueLabel.setText("");
        }

        // Max drawdown (assumed decimal like 0.056)
        if (state.getMaxDrawdown() != null) {
            drawdownLabel.setText("Max Drawdown: " + percentFormat.format(state.getMaxDrawdown()));
        } else {
            drawdownLabel.setText("");
        }

        // Total return (already a percent or decimal? we stored as percent, so just show)
        if (state.getTotalReturn() != null) {
            returnLabel.setText("Total Return: " + state.getTotalReturn() + " %");
        } else {
            returnLabel.setText("");
        }
    }

    public String getViewName() {
        return RunBacktestViewModel.VIEW_NAME;
    }
}