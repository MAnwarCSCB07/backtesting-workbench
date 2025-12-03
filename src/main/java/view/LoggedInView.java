package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logged into the program.
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "logged in";

    private ChangePasswordController changePasswordController = null;
    private LogoutController logoutController = null;

    private final JLabel usernameLabel;

    // NEW: Run Backtest button reference
    private final JButton runBacktestButton = new JButton("Run Backtest");

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {

        loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);

        // Username centered
        usernameLabel = new JLabel("user: ");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(usernameLabel);

        this.add(Box.createVerticalStrut(40));

        JButton alphaButton = new JButton("Alpha Vantage");
        alphaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(alphaButton);

        this.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        this.add(separator);
        this.add(Box.createVerticalStrut(10));

        JButton inputCsvButton = new JButton("Input Stock Data (CSV)");
        inputCsvButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(inputCsvButton);

        this.add(Box.createVerticalStrut(20));

        runBacktestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(runBacktestButton);

        // --- Navigation Wiring ---

        alphaButton.addActionListener(e -> {
            viewManagerModel.setState("alpha vantage");
            viewManagerModel.firePropertyChange();
        });

        inputCsvButton.addActionListener(e -> {
            viewManagerModel.setState("input stock data");
            viewManagerModel.firePropertyChange();
        });

        // NEW: navigation to Run Backtest
        runBacktestButton.addActionListener(e -> {
            viewManagerModel.setState("run backtest");
            viewManagerModel.firePropertyChange();
        });
    }

    /**
     * Handles logout button clicks (if logout button triggers this view).
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (logoutController != null) {
            logoutController.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            usernameLabel.setText("user: " + state.getUsername());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
}