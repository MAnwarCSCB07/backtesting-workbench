package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;

public class ConfigureFactorsView extends JPanel {
    public final String viewName = "configure factors";

    public ConfigureFactorsView(ViewManagerModel viewManagerModel) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);

        JLabel title = new JLabel("Configure Factors Page");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(title);

        this.add(Box.createVerticalStrut(40));
        JPanel FactorOptionsPanel = new JPanel();
        FactorOptionsPanel.setLayout(new BoxLayout(FactorOptionsPanel, BoxLayout.Y_AXIS));
        FactorOptionsPanel.setBorder(BorderFactory.createTitledBorder("Factor Options"));

        JPanel PreprocessingOptionsPanel = new JPanel();
        PreprocessingOptionsPanel.setLayout(new BoxLayout(PreprocessingOptionsPanel, BoxLayout.Y_AXIS));
        PreprocessingOptionsPanel.setBorder(BorderFactory.createTitledBorder("Preprocessing Options"));

        // Mapping from UI label to backend enum value
        java.util.LinkedHashMap<String, String> factorLabelToEnum = new java.util.LinkedHashMap<>();
        factorLabelToEnum.put("Momentum", "MOMENTUM_12_1");
        factorLabelToEnum.put("Value", "VALUE_PROXY");
        factorLabelToEnum.put("Size", "SIZE");
        factorLabelToEnum.put("Quality", "REVERSAL_1_1");
        factorLabelToEnum.put("Volatility", "LOW_VOL");

        // Store checkboxes for later retrieval
        java.util.LinkedHashMap<String, JCheckBox> factorCheckBoxes = new java.util.LinkedHashMap<>();
        for (String label : factorLabelToEnum.keySet()) {
            JCheckBox checkBox = new JCheckBox(label);
            factorCheckBoxes.put(label, checkBox);
            FactorOptionsPanel.add(checkBox);
        }

        // Preprocessing options radio buttons (mutually exclusive)
        JRadioButton winsorizeRadio = new JRadioButton("Winsorize");
        JRadioButton zScoreRadio = new JRadioButton("Z-Score");
        JRadioButton noneRadio = new JRadioButton("None");
        ButtonGroup preprocessingGroup = new ButtonGroup();
        preprocessingGroup.add(winsorizeRadio);
        preprocessingGroup.add(zScoreRadio);
        preprocessingGroup.add(noneRadio);

        // Add preprocessing options
        PreprocessingOptionsPanel.add(winsorizeRadio);
        PreprocessingOptionsPanel.add(zScoreRadio);
        PreprocessingOptionsPanel.add(noneRadio);
        // Add panels to main view
        this.add(FactorOptionsPanel);
        this.add(Box.createVerticalStrut(20));
        this.add(PreprocessingOptionsPanel);


        JButton back = new JButton("Return");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> {
            // For now return to the Logged In view for consistency
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });
        this.add(back);
    }
}
