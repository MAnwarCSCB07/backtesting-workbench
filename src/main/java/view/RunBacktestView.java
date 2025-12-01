package view;

import interface_adapter.run_backtest.RunBacktestController;
import interface_adapter.run_backtest.RunBacktestState;
import interface_adapter.run_backtest.RunBacktestViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

public class RunBacktestView extends JPanel implements PropertyChangeListener {

    private RunBacktestController controller;              // set later by AppBuilder
    private final RunBacktestViewModel viewModel;

    // Inputs
    private final JTextField projectIdField   = new JTextField(15);
    private final JTextField tickerField      = new JTextField(10);
    private final JTextField capitalField     = new JTextField(10);
    private final JTextField startDateField   = new JTextField(10);
    private final JTextField endDateField     = new JTextField(10);
    private final JTextField riskFreeField    = new JTextField(6);

    // Actions
    private final JButton runButton           = new JButton("Run Backtest");
    private final JButton showCurveButton     = new JButton("Show Equity Curve");

    // Outputs
    private final JLabel statusLabel      = new JLabel();
    private final JLabel finalValueLabel  = new JLabel();
    private final JLabel drawdownLabel    = new JLabel();
    private final JLabel returnLabel      = new JLabel();
    private final JLabel annReturnLabel   = new JLabel();
    private final JLabel volLabel         = new JLabel();
    private final JLabel sharpeLabel      = new JLabel();
    private final JLabel worstLossLabel   = new JLabel();

    public static final String VIEW_NAME = RunBacktestViewModel.VIEW_NAME;

    public RunBacktestView(RunBacktestViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // -------- Top: settings --------
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Backtest Settings"));
        settingsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Defaults
        projectIdField.setText("demo-project");
        tickerField.setText("AAPL");
        capitalField.setText("10000");
        startDateField.setText(LocalDate.now().minusDays(27).toString());
        endDateField.setText(LocalDate.now().toString());
        riskFreeField.setText("4.5");

        int row = 0;

        // row 0: Project ID
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(new JLabel("Project ID:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.add(projectIdField, gbc);
        row++;

        // row 1: Ticker
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(new JLabel("Ticker:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.add(tickerField, gbc);
        row++;

        // row 2: Initial capital
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(new JLabel("Initial Capital ($):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.add(capitalField, gbc);
        row++;

        // row 3: Start date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(new JLabel("Start Date (YYYY-MM-DD, max ~100 days):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.add(startDateField, gbc);
        row++;

        // row 4: End date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.add(endDateField, gbc);
        row++;

        // row 5: Risk-free rate
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(new JLabel("Risk-free Rate (% per year):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.add(riskFreeField, gbc);

        add(settingsPanel, BorderLayout.NORTH);

        // -------- Middle: buttons --------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(runButton);
        buttonPanel.add(showCurveButton);
        add(buttonPanel, BorderLayout.CENTER);

        // -------- Bottom: results --------
        JPanel resultsPanel = new JPanel();
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Backtest Results"));
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        resultsPanel.add(statusLabel);
        resultsPanel.add(finalValueLabel);
        resultsPanel.add(drawdownLabel);
        resultsPanel.add(returnLabel);
        resultsPanel.add(annReturnLabel);
        resultsPanel.add(volLabel);
        resultsPanel.add(sharpeLabel);
        resultsPanel.add(worstLossLabel);

        add(resultsPanel, BorderLayout.SOUTH);

        // -------- Button actions --------

        // Run backtest
        runButton.addActionListener(e -> {
            if (controller == null) return;

            String projectId = projectIdField.getText().trim();
            String ticker    = tickerField.getText().trim();
            String capital   = capitalField.getText().trim();
            String start     = startDateField.getText().trim();
            String end       = endDateField.getText().trim();
            String rf        = riskFreeField.getText().trim();

            controller.runBacktest(projectId, ticker, capital, start, end, rf);
        });

        // Show equity curve
        showCurveButton.addActionListener(e -> {
            RunBacktestState state = viewModel.getState();
            if (state == null || state.getEquityCurve() == null || state.getEquityCurve().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No equity curve data. Run a backtest first.",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            EquityCurveChartView.showEquityCurve(state.getEquityCurve());
        });
    }

    // Called by AppBuilder after controller is created
    public void setRunBacktestController(RunBacktestController controller) {
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        RunBacktestState state = viewModel.getState();
        if (state == null) return;

        statusLabel.setText(state.getStatusMessage() == null ? "" : state.getStatusMessage());

        if (state.getFinalValue() != null) {
            finalValueLabel.setText(String.format("Final Value: $%.2f", state.getFinalValue()));
        } else finalValueLabel.setText("");

        if (state.getMaxDrawdown() != null) {
            drawdownLabel.setText(String.format("Max Drawdown: %.2f %%", state.getMaxDrawdown() * 100.0));
        } else drawdownLabel.setText("");

        if (state.getTotalReturn() != null) {
            returnLabel.setText(String.format("Total Return: %.4f %%", state.getTotalReturn()));
        } else returnLabel.setText("");

        if (state.getAnnualizedReturn() != null) {
            annReturnLabel.setText(String.format("Annualized Return: %.4f %%", state.getAnnualizedReturn()));
        } else annReturnLabel.setText("");

        if (state.getVolatility() != null) {
            volLabel.setText(String.format("Volatility (Annualized): %.4f %%", state.getVolatility()));
        } else volLabel.setText("");

        if (state.getSharpeRatio() != null) {
            sharpeLabel.setText(String.format("Sharpe Ratio: %.4f", state.getSharpeRatio()));
        } else sharpeLabel.setText("");

        if (state.getWorstDailyLoss() != null) {
            worstLossLabel.setText(String.format("Worst Daily Loss: %.4f %%", state.getWorstDailyLoss()));
        } else worstLossLabel.setText("");
    }

    public String getViewName() {
        return VIEW_NAME;
    }
}