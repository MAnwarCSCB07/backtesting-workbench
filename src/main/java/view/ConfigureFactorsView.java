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

        // Factor options checkboxes
        JCheckBox momentumCheck = new JCheckBox("Momentum");
        JCheckBox valueCheck = new JCheckBox("Value");
        JCheckBox sizeCheck = new JCheckBox("Size");
        JCheckBox qualityCheck = new JCheckBox("Quality");
        JCheckBox volatilityCheck = new JCheckBox("Volatility");

        // Add factor options
        FactorOptionsPanel.add(momentumCheck);
        FactorOptionsPanel.add(valueCheck);
        FactorOptionsPanel.add(sizeCheck);
        FactorOptionsPanel.add(qualityCheck);
        FactorOptionsPanel.add(volatilityCheck);

        // Preprocessing options checkboxes
        JCheckBox winsorizeCheck = new JCheckBox("Winsorize");
        JCheckBox standardizeCheck = new JCheckBox("Standardize");
        JCheckBox neutralizeCheck = new JCheckBox("Market Neutralize");

        // Add preprocessing options
        PreprocessingOptionsPanel.add(winsorizeCheck);
        PreprocessingOptionsPanel.add(standardizeCheck);
        PreprocessingOptionsPanel.add(neutralizeCheck);

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
