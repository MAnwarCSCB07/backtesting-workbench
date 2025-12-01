package view;

import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;
import interface_adapter.ViewManagerModel;
import interface_adapter.factor_config.FactorConfigController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ConfigureFactorsView extends JPanel {
    public final String viewName = "configure factors";

    private FactorConfigController controller;

    public void setFactorConfigController(FactorConfigController controller) {
        this.controller = controller;
    }

    public ConfigureFactorsView(ViewManagerModel viewManagerModel) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Configure Factors");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        this.add(Box.createVerticalStrut(10));
        this.add(title);

        this.add(Box.createVerticalStrut(20));
        JPanel FactorOptionsPanel = new JPanel();
        FactorOptionsPanel.setLayout(new BoxLayout(FactorOptionsPanel, BoxLayout.Y_AXIS));
        FactorOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Factor Options"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JPanel PreprocessingOptionsPanel = new JPanel();
        PreprocessingOptionsPanel.setLayout(new BoxLayout(PreprocessingOptionsPanel, BoxLayout.Y_AXIS));
        PreprocessingOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Preprocessing Options"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JPanel SymbolsPanel = new JPanel();
        SymbolsPanel.setLayout(new BorderLayout(8, 8));
        SymbolsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Symbols (comma-separated)"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JTextField symbolsField = new JTextField("AAA, BBB, CCC");
        symbolsField.setToolTipText("Enter tickers like: AAPL, MSFT, GOOG");
        SymbolsPanel.add(symbolsField, BorderLayout.CENTER);

        // Mapping from UI label to backend enum value
        java.util.LinkedHashMap<String, Factor> factorLabelToEnum = new java.util.LinkedHashMap<>();
        factorLabelToEnum.put("Momentum", Factor.MOMENTUM_12_1);
        factorLabelToEnum.put("Volatility (Low Vol)", Factor.LOW_VOL);
        // Placeholders for future factors available in enum
        factorLabelToEnum.put("Value", Factor.VALUE_PROXY);
        factorLabelToEnum.put("Size", Factor.SIZE);
        factorLabelToEnum.put("Quality (Short-term Reversal)", Factor.REVERSAL_1_1);

        // Store checkboxes for later retrieval
        java.util.LinkedHashMap<String, JCheckBox> factorCheckBoxes = new java.util.LinkedHashMap<>();
        for (String label : factorLabelToEnum.keySet()) {
            JCheckBox checkBox = new JCheckBox(label);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        noneRadio.setSelected(true);

        // Add preprocessing options
        PreprocessingOptionsPanel.add(winsorizeRadio);
        PreprocessingOptionsPanel.add(zScoreRadio);
        PreprocessingOptionsPanel.add(noneRadio);
        // Add panels to main view
        this.add(SymbolsPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(FactorOptionsPanel);
        this.add(Box.createVerticalStrut(20));
        this.add(PreprocessingOptionsPanel);

        this.add(Box.createVerticalStrut(20));
        JButton compute = new JButton("Compute Rankings");
        compute.setAlignmentX(Component.CENTER_ALIGNMENT);
        compute.setToolTipText("Compute composite factor ranks for the entered symbols");
        compute.setPreferredSize(new Dimension(220, 36));
        compute.setMaximumSize(new Dimension(240, 40));
        compute.addActionListener(e -> {
            if (controller == null) {
                JOptionPane.showMessageDialog(this, "Controller not configured.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Symbols parsing
            String text = symbolsField.getText().trim();
            List<String> symbols = new ArrayList<>();
            if (!text.isEmpty()) {
                for (String s : text.split(",")) {
                    String sym = s.trim();
                    if (!sym.isEmpty()) symbols.add(sym);
                }
            }
            if (symbols.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter at least one symbol.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Selected factors
            List<Factor> selected = new ArrayList<>();
            for (Map.Entry<String, JCheckBox> entry : factorCheckBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selected.add(factorLabelToEnum.get(entry.getKey()));
                }
            }
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one factor.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Equal weights for selected factors for now
            Map<Factor, Double> weights = new EnumMap<>(Factor.class);
            double w = 1.0 / selected.size();
            for (Factor f : selected) weights.put(f, w);

            // Preprocessing selection
            PreprocessingMethod method = PreprocessingMethod.NONE;
            if (zScoreRadio.isSelected()) method = PreprocessingMethod.Z_SCORE;
            else if (winsorizeRadio.isSelected()) method = PreprocessingMethod.WINSORIZE;

            controller.execute(symbols, selected, weights, method);
        });

        this.add(compute);


        JButton back = new JButton("Return");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setPreferredSize(new Dimension(160, 32));
        back.setMaximumSize(new Dimension(200, 36));
        back.addActionListener(e -> {
            // For now return to the Logged In view for consistency
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });
        this.add(back);
    }
}
