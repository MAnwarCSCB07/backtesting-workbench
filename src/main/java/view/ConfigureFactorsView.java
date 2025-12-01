package view;

import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;
import interface_adapter.ViewManagerModel;
import interface_adapter.factor_config.FactorConfigController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigureFactorsView extends JPanel {
    public final String viewName = "configure factors";

    private FactorConfigController controller;

    // UI Constants for a "Web" feel
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246); // Modern Blue
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // Dark Gray
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Form elements
    private final JTextField symbolsField;
    private final LinkedHashMap<String, JCheckBox> factorCheckBoxes;
    private final JRadioButton winsorizeRadio;
    private final JRadioButton zScoreRadio;
    private final JRadioButton noneRadio;

    public void setFactorConfigController(FactorConfigController controller) {
        this.controller = controller;
    }

    public ConfigureFactorsView(ViewManagerModel viewManagerModel) {
        // 1. Main Container Setup (The "Page")
        this.setLayout(new GridBagLayout()); // Centers the card
        this.setBackground(BG_COLOR);

        // 2. The "Card" Panel (The white box in the middle)
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(40, 40, 40, 40) // Internal Padding
        ));

        // --- HEADER ---
        JLabel titleLabel = new JLabel("Configure Analysis");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Select symbols and factors to generate rankings.");
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- SECTION 1: SYMBOLS ---
        JLabel symbolsLabel = new JLabel("Target Symbols");
        symbolsLabel.setFont(SECTION_FONT);
        symbolsLabel.setForeground(TEXT_COLOR);
        symbolsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        symbolsField = new JTextField("TSLA, NVDA, AAPL");
        symbolsField.setFont(BODY_FONT);
        symbolsField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 1),
                new EmptyBorder(8, 10, 8, 10))
        );
        symbolsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        symbolsField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- SECTION 2: FACTORS (Grid) ---
        JLabel factorsLabel = new JLabel("Select Factors");
        factorsLabel.setFont(SECTION_FONT);
        factorsLabel.setForeground(TEXT_COLOR);
        factorsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel factorsGrid = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 Columns
        factorsGrid.setBackground(CARD_BG);
        factorsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        factorsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        LinkedHashMap<String, Factor> factorLabelToEnum = new LinkedHashMap<>();
        factorLabelToEnum.put("Momentum (3M)", Factor.MOMENTUM_12_1); // Updated Label for short term
        factorLabelToEnum.put("Volatility", Factor.LOW_VOL);
        factorLabelToEnum.put("Value Proxy", Factor.VALUE_PROXY);
        factorLabelToEnum.put("Size", Factor.SIZE);
        factorLabelToEnum.put("Reversal", Factor.REVERSAL_1_1);

        factorCheckBoxes = new LinkedHashMap<>();
        for (String label : factorLabelToEnum.keySet()) {
            JCheckBox cb = new JCheckBox(label);
            cb.setFont(BODY_FONT);
            cb.setBackground(CARD_BG);
            cb.setFocusPainted(false);
            factorCheckBoxes.put(label, cb);
            factorsGrid.add(cb);
        }

        // --- SECTION 3: PREPROCESSING ---
        JLabel processLabel = new JLabel("Data Cleaning");
        processLabel.setFont(SECTION_FONT);
        processLabel.setForeground(TEXT_COLOR);
        processLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setBackground(CARD_BG);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        winsorizeRadio = createStyledRadio("Winsorize");
        zScoreRadio = createStyledRadio("Z-Score");
        noneRadio = createStyledRadio("None");
        noneRadio.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(winsorizeRadio);
        group.add(zScoreRadio);
        group.add(noneRadio);

        radioPanel.add(noneRadio);
        radioPanel.add(Box.createHorizontalStrut(15));
        radioPanel.add(winsorizeRadio);
        radioPanel.add(Box.createHorizontalStrut(15));
        radioPanel.add(zScoreRadio);

        // --- BUTTONS ---
        JButton computeBtn = new JButton("Run Analysis");
        stylePrimaryButton(computeBtn);

        JButton backBtn = new JButton("← Back to Dashboard");
        styleLinkButton(backBtn);

        // --- ASSEMBLY ---
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(25));

        cardPanel.add(symbolsLabel);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(symbolsField);
        cardPanel.add(Box.createVerticalStrut(20));

        cardPanel.add(factorsLabel);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(factorsGrid);
        cardPanel.add(Box.createVerticalStrut(20));

        cardPanel.add(processLabel);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(radioPanel);
        cardPanel.add(Box.createVerticalStrut(30));

        cardPanel.add(computeBtn);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(backBtn);

        // Add Card to the Main View (Centered)
        this.add(cardPanel);

        JButton saveExportButton = new JButton("Save & Export");
        saveExportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveExportButton.addActionListener(e -> {
            viewManagerModel.setState("save export");
            viewManagerModel.firePropertyChange();
        });
        this.add(saveExportButton);

        this.add(Box.createVerticalStrut(20));
        // --- EVENT LISTENERS ---

        // 1. Back Button
        backBtn.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        // 2. Compute Button
        computeBtn.addActionListener(e -> {
            if (controller == null) {
                showError("Controller configuration missing.");
                return;
            }

            // Parse Symbols
            String text = symbolsField.getText().trim();
            List<String> symbols = new ArrayList<>();
            if (!text.isEmpty()) {
                for (String s : text.split(",")) {
                    String sym = s.trim();
                    if (!sym.isEmpty()) symbols.add(sym);
                }
            }

            if (symbols.isEmpty()) {
                showError("Please enter at least one symbol.");
                return;
            }

            // Parse Factors
            List<Factor> selected = new ArrayList<>();
            for (Map.Entry<String, JCheckBox> entry : factorCheckBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    // Re-map labels to enums carefully
                    // Note: We iterate over the checkboxes we stored.
                    // We need to look up the enum based on the label.
                    Factor f = factorLabelToEnum.get(entry.getKey());
                    if (f != null) selected.add(f);
                }
            }

            if (selected.isEmpty()) {
                showError("Please select at least one factor to analyze.");
                return;
            }

            // Weights (Equal weighting strategy)
            Map<Factor, Double> weights = new EnumMap<>(Factor.class);
            double w = 1.0 / selected.size();
            for (Factor f : selected) weights.put(f, w);

            // Preprocessing
            PreprocessingMethod method = PreprocessingMethod.NONE;
            if (zScoreRadio.isSelected()) method = PreprocessingMethod.Z_SCORE;
            else if (winsorizeRadio.isSelected()) method = PreprocessingMethod.WINSORIZE;

            // Execute
            controller.execute(symbols, selected, weights, method);
        });
    }

    // --- HELPER METHODS FOR STYLING ---

    private JRadioButton createStyledRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(BODY_FONT);
        rb.setBackground(CARD_BG);
        rb.setFocusPainted(false);
        return rb;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_COLOR);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(PRIMARY_COLOR.darker());
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(PRIMARY_COLOR);
            }
        });
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.GRAY);
        btn.setBackground(CARD_BG);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setForeground(PRIMARY_COLOR);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setForeground(Color.GRAY);
            }
        });
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}