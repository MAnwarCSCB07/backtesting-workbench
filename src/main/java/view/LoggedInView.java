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
    private LogoutController logoutController;

    private final JLabel usernameLabel;

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);

        // Username centered
        usernameLabel = new JLabel("user: ");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(usernameLabel);

        // Top margin before buttons
        this.add(Box.createVerticalStrut(40));

        // Alpha Vantage button
        JButton alphaButton = new JButton("Alpha Vantage");
        alphaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(alphaButton);

        // Separator line
        this.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        this.add(separator);
        this.add(Box.createVerticalStrut(10));

        // Input Stock Data button
        JButton inputCsvButton = new JButton("Input Stock Data (CSV)");
        inputCsvButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(inputCsvButton);

        // Separator line
        this.add(Box.createVerticalStrut(10));
        JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        this.add(separator2);
        this.add(Box.createVerticalStrut(10));

        // Save & Export button
        JButton saveExportButton = new JButton("Save & Export");
        saveExportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(saveExportButton);

        // Wire navigation
        alphaButton.addActionListener(e -> {
            viewManagerModel.setState("alpha vantage");
            viewManagerModel.firePropertyChange();
        });

        inputCsvButton.addActionListener(e -> {
            viewManagerModel.setState("input stock data");
            viewManagerModel.firePropertyChange();
        });

        saveExportButton.addActionListener(e -> {
            viewManagerModel.setState("save export");
            viewManagerModel.firePropertyChange();
        });
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        logoutController.execute();
        if (logoutController != null) {
            logoutController.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
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
