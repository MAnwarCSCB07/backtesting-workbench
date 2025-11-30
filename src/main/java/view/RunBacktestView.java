package view;

import interface_adapter.run_backtest.RunBacktestController;
import interface_adapter.run_backtest.RunBacktestState;
import interface_adapter.run_backtest.RunBacktestViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RunBacktestView extends JPanel implements PropertyChangeListener {

    private RunBacktestController controller;              // will be set later by AppBuilder
    private final RunBacktestViewModel viewModel;

    private final JTextField projectIdField = new JTextField(15);
    private final JButton runButton = new JButton("Run Backtest");
    private final JLabel statusLabel = new JLabel();
    private final JLabel finalValueLabel = new JLabel();
    private final JLabel drawdownLabel = new JLabel();

    public RunBacktestView(RunBacktestViewModel viewModel) {
        this.viewModel = viewModel;

        // Listen for changes from the ViewModel
        this.viewModel.addPropertyChangeListener(this);

        // Basic layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Project ID:"));
        add(projectIdField);
        add(Box.createRigidArea(new Dimension(0, 10)));

        add(runButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        add(statusLabel);
        add(finalValueLabel);
        add(drawdownLabel);

        runButton.addActionListener(e -> {
            if (controller == null) {
                // Not wired yet; do nothing to avoid NullPointerException
                return;
            }
            String projectId = projectIdField.getText();
            controller.runBacktest(projectId);
        });
    }

    // Called by AppBuilder after the controller is created
    public void setRunBacktestController(RunBacktestController controller) {
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        RunBacktestState state = viewModel.getState();

        statusLabel.setText(state.getStatusMessage());

        if (state.getFinalValue() != null) {
            finalValueLabel.setText("Final Value: " + state.getFinalValue());
        } else {
            finalValueLabel.setText("");
        }

        if (state.getMaxDrawdown() != null) {
            drawdownLabel.setText("Max Drawdown: " + state.getMaxDrawdown());
        } else {
            drawdownLabel.setText("");
        }
    }

    public String getViewName() {
        return RunBacktestViewModel.VIEW_NAME;
    }
}