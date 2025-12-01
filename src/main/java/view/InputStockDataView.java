package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.import_ohlcv.ImportOHLCVController;
import interface_adapter.import_ohlcv.ImportOHLCVViewModel;
import interface_adapter.import_ohlcv.ImportOHLCVState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * UC-1 View: Import OHLCV data (CSV/API) into a project.
 */
public class InputStockDataView extends JPanel implements PropertyChangeListener {

    public final String viewName = "input stock data";

    private final ImportOHLCVViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    private ImportOHLCVController controller;

    private final JTextField projectIdField = new JTextField(15);
    private final JTextField tickersField   = new JTextField(25);   // comma-separated
    private final JTextField startDateField = new JTextField(10);   // YYYY-MM-DD
    private final JTextField endDateField   = new JTextField(10);   // YYYY-MM-DD

    private final JComboBox<String> sourceCombo =
            new JComboBox<>(new String[]{"Alpha Vantage API", "Local CSV (TODO)"});

    private final JButton importButton           = new JButton("Import Prices");
    private final JButton configureFactorsButton = new JButton("Configure Factors");
    private final JButton backButton             = new JButton("Return");

    private final JLabel statusLabel = new JLabel();
    private final JTextArea detailsArea = new JTextArea(8, 40);

    public InputStockDataView(ImportOHLCVViewModel viewModel,
                              ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Input Stock Data (Import OHLCV)", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        // Center panel
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(buildFormPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(buildButtonsPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(buildStatusPanel());

        add(center, BorderLayout.CENTER);

        refreshFromViewModel();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Project ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Project ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(projectIdField, gbc);
        row++;

        // Tickers
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Tickers (comma-separated):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(tickersField, gbc);
        row++;

        // Start date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(startDateField, gbc);
        row++;

        // End date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(endDateField, gbc);
        row++;

        // Source
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Source:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(sourceCombo, gbc);

        return form;
    }

    private JPanel buildButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        panel.add(importButton);
        panel.add(configureFactorsButton);
        panel.add(backButton);

        // Import
        importButton.addActionListener(e -> {
            if (controller == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Import controller not wired yet.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String projectId = projectIdField.getText().trim();
            String tickers   = tickersField.getText().trim();
            String start     = startDateField.getText().trim();
            String end       = endDateField.getText().trim();
            String source    = (String) sourceCombo.getSelectedItem();

            controller.importPrices(projectId, tickers, start, end, source);
        });

        // Configure Factors
        configureFactorsButton.addActionListener(e -> {
            viewManagerModel.setState("configure factors");
            viewManagerModel.firePropertyChange();
        });

        // Back
        backButton.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        return panel;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setOpaque(false);

        statusLabel.setForeground(Color.DARK_GRAY);

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(detailsArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public void setImportController(ImportOHLCVController controller) {
        this.controller = controller;
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            refreshFromViewModel();
        }
    }

    private void refreshFromViewModel() {
        ImportOHLCVState state = viewModel.getState();
        if (state == null) {
            statusLabel.setText("");
            detailsArea.setText("");
            return;
        }

        if (state.getStatusMessage() != null) {
            statusLabel.setText(state.getStatusMessage());
        } else {
            statusLabel.setText("");
        }

        StringBuilder sb = new StringBuilder();

        List<String> loaded = state.getLoadedTickers();
        if (loaded != null && !loaded.isEmpty()) {
            sb.append("Loaded tickers:\n");
            for (String t : loaded) {
                sb.append("  • ").append(t).append('\n');
            }
            sb.append('\n');
        }

        List<String> missing = state.getMissingTickers();
        if (missing != null && !missing.isEmpty()) {
            sb.append("Missing / failed tickers:\n");
            for (String t : missing) {
                sb.append("  • ").append(t).append('\n');
            }
        }

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }
}
