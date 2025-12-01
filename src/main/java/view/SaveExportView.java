package view;

import interface_adapter.save_export.SaveExportController;
import interface_adapter.save_export.SaveExportState;
import interface_adapter.save_export.SaveExportViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Save/Export Use Case.
 * Allows users to save and export their backtesting projects.
 */
public class SaveExportView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "save export";
    private final SaveExportViewModel saveExportViewModel;
    private SaveExportController saveExportController = null;

    private final JTextField projectIdInputField = new JTextField(15);
    private final JTextField projectNameInputField = new JTextField(15);
    private final JComboBox<String> exportTypeComboBox = new JComboBox<>(new String[]{"CSV", "HTML", "BOTH", "JSON"});
    private final JTextField filePathInputField = new JTextField(15);
    private final JButton saveExportButton;
    private final JButton cancelButton;
    private final JLabel messageLabel = new JLabel();
    private final JLabel errorLabel = new JLabel();

    public SaveExportView(SaveExportViewModel saveExportViewModel) {
        this.saveExportViewModel = saveExportViewModel;
        this.saveExportViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel(SaveExportViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(title);

        this.add(Box.createVerticalStrut(20));

        // Project ID field
        LabelTextPanel projectIdInfo = new LabelTextPanel(
                new JLabel(SaveExportViewModel.PROJECT_ID_LABEL), projectIdInputField);
        this.add(projectIdInfo);

        // Project Name field
        LabelTextPanel projectNameInfo = new LabelTextPanel(
                new JLabel(SaveExportViewModel.PROJECT_NAME_LABEL), projectNameInputField);
        this.add(projectNameInfo);

        // Export Type field
        JPanel exportTypePanel = new JPanel();
        exportTypePanel.add(new JLabel(SaveExportViewModel.EXPORT_TYPE_LABEL));
        exportTypePanel.add(exportTypeComboBox);
        exportTypeComboBox.setSelectedItem("BOTH"); // Default
        this.add(exportTypePanel);

        // File Path field (optional)
        LabelTextPanel filePathInfo = new LabelTextPanel(
                new JLabel(SaveExportViewModel.FILE_PATH_LABEL), filePathInputField);
        this.add(filePathInfo);

        // Message and error labels
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(Color.GREEN.darker());
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setForeground(Color.RED);
        this.add(Box.createVerticalStrut(10));
        this.add(messageLabel);
        this.add(errorLabel);

        // Buttons
        JPanel buttons = new JPanel();
        saveExportButton = new JButton(SaveExportViewModel.SAVE_EXPORT_BUTTON_LABEL);
        cancelButton = new JButton(SaveExportViewModel.CANCEL_BUTTON_LABEL);
        buttons.add(saveExportButton);
        buttons.add(cancelButton);
        this.add(Box.createVerticalStrut(20));
        this.add(buttons);

        // Add action listeners
        saveExportButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(saveExportButton)) {
                            SaveExportState currentState = saveExportViewModel.getState();
                            String filePath = currentState.getFilePath();
                            if (filePath == null || filePath.isEmpty()) {
                                filePath = null; // Use default path
                            }
                            saveExportController.execute(
                                    currentState.getProjectId(),
                                    currentState.getProjectName(),
                                    currentState.getExportType(),
                                    filePath
                            );
                        }
                    }
                }
        );

        cancelButton.addActionListener(this);

        // Add document listeners for real-time state updates
        addProjectIdListener();
        addProjectNameListener();
        addExportTypeListener();
        addFilePathListener();
    }

    private void addProjectIdListener() {
        projectIdInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                SaveExportState currentState = saveExportViewModel.getState();
                currentState.setProjectId(projectIdInputField.getText());
                saveExportViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
    }

    private void addProjectNameListener() {
        projectNameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                SaveExportState currentState = saveExportViewModel.getState();
                currentState.setProjectName(projectNameInputField.getText());
                saveExportViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
    }

    private void addExportTypeListener() {
        exportTypeComboBox.addActionListener(e -> {
            SaveExportState currentState = saveExportViewModel.getState();
            currentState.setExportType((String) exportTypeComboBox.getSelectedItem());
            saveExportViewModel.setState(currentState);
        });
    }

    private void addFilePathListener() {
        filePathInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                SaveExportState currentState = saveExportViewModel.getState();
                currentState.setFilePath(filePathInputField.getText());
                saveExportViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(cancelButton)) {
            // Clear fields and return to previous view
            SaveExportState state = saveExportViewModel.getState();
            state.setProjectId("");
            state.setProjectName("");
            state.setFilePath("");
            state.setMessage("");
            state.setErrorMessage("");
            saveExportViewModel.setState(state);
            saveExportViewModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SaveExportState state = (SaveExportState) evt.getNewValue();
        setFields(state);
        
        // Update message and error labels
        if (state.getMessage() != null && !state.getMessage().isEmpty()) {
            messageLabel.setText("<html>" + state.getMessage().replace("\n", "<br>") + "</html>");
            errorLabel.setText("");
        } else {
            messageLabel.setText("");
        }
        
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            errorLabel.setText(state.getErrorMessage());
            messageLabel.setText("");
        } else {
            errorLabel.setText("");
        }
    }

    private void setFields(SaveExportState state) {
        projectIdInputField.setText(state.getProjectId());
        projectNameInputField.setText(state.getProjectName());
        exportTypeComboBox.setSelectedItem(state.getExportType());
        filePathInputField.setText(state.getFilePath());
    }

    public String getViewName() {
        return viewName;
    }

    public void setSaveExportController(SaveExportController controller) {
        this.saveExportController = controller;
    }
}


