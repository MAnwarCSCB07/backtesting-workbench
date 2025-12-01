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
import java.util.LinkedHashMap;

public class ConfigureFactorsView extends JPanel {
    public final String viewName = "configure factors";

    private FactorConfigController controller;

    public void setFactorConfigController(FactorConfigController controller) {
        this.controller = controller;
    }

    public ConfigureFactorsView(ViewManagerModel viewManagerModel) {
        // 1. Main View Setup
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 2. Title
        JLabel title = new JLabel("Configure Factors");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        this.add(Box.createVerticalStrut(10));
        this.add(title);
        this.add(Box.createVerticalStrut(20));

        // 3. Symbols Panel
        JPanel symbolsPanel = new JPanel();
        symbolsPanel.setLayout(new BorderLayout(8, 8));
        symbolsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Symbols (comma-separated)"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JTextField symbolsField = new JTextField("AAA, BBB, CCC");
        symbolsField.setToolTipText("Enter tickers like: AAPL, MSFT, GOOG");
        symbolsPanel.add(symbolsField, BorderLayout.CENTER);

        // 4. Factor Options Panel
        JPanel factorOptionsPanel = new JPanel();
        factorOptionsPanel.setLayout(new BoxLayout(factorOptionsPanel, BoxLayout.Y_AXIS));
        factorOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Factor Options"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        // Mapping from UI label to backend enum value
        LinkedHashMap<String, Factor> factorLabelToEnum = new LinkedHashMap<>();
        factorLabelToEnum.put("Momentum", Factor.MOMENTUM_12_1);
        factorLabelToEnum.put("Volatility (Low Vol)", Factor.LOW_VOL);
        factorLabelToEnum.put("Value", Factor.VALUE_PROXY);
        factorLabelToEnum.put("Size", Factor.SIZE);
        factorLabelToEnum.put("Quality (Short-term Reversal)", Factor.REVERSAL_1_1);

        // Store checkboxes for later retrieval
        LinkedHashMap<String, JCheckBox> factorCheckBoxes = new LinkedHashMap<>();
        for (String label : factorLabelToEnum.keySet()) {
            JCheckBox checkBox = new JCheckBox(label);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            factorCheckBoxes.put(label, checkBox);
            factorOptionsPanel.add(checkBox);
        }

        // 5. Preprocessing Panel
        JPanel preprocessingOptionsPanel = new JPanel();
        preprocessingOptionsPanel.setLayout(new BoxLayout(preprocessingOptionsPanel, BoxLayout.Y_AXIS));
        preprocessingOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Preprocessing Options"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JRadioButton winsorizeRadio = new JRadioButton("Winsorize");
        JRadioButton zScoreRadio = new JRadioButton("Z-Score");
        JRadioButton noneRadio = new JRadioButton("None");

        ButtonGroup preprocessingGroup = new ButtonGroup();
        preprocessingGroup.add(winsorizeRadio);
        preprocessingGroup.add(zScoreRadio);
        preprocessingGroup.add(noneRadio);
        noneRadio.setSelected(true); // Default selection

        preprocessingOptionsPanel.add(winsorizeRadio);
        preprocessingOptionsPanel.add(zScoreRadio);
        preprocessingOptionsPanel.add(noneRadio);

        // 6. Add Panels to Main View
        this.add(symbolsPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(factorOptionsPanel);
        this.add(Box.createVerticalStrut(20));
        this.add(preprocessingOptionsPanel);
        this.add(Box.createVerticalStrut(20));

        // 7. Compute Button
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

        // 8. Back Button
        this.add(Box.createVerticalStrut(10)); // Gap between buttons
        JButton back = new JButton("Return");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setPreferredSize(new Dimension(160, 32));
        back.setMaximumSize(new Dimension(200, 36));
        back.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });
        this.add(back);
    }
}
