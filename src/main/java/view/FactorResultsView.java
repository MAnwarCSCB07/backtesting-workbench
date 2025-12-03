package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.factor_config.FactorViewModel;
import interface_adapter.factor_config.FactorViewState;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Modernized view to display factor ranking results.
 * Fixed: Card now auto-expands to fit text to prevent truncation.
 */
public class FactorResultsView extends JPanel implements PropertyChangeListener {

    private final FactorViewModel viewModel;
    private final DefaultTableModel tableModel;

    // UI Constants
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color HEADER_BG = new Color(249, 250, 251);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public FactorResultsView(FactorViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        // 1. Main Layout
        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);

        // 2. The Card Panel
        // FIX: Removed fixed setPreferredSize.
        // We use a custom JPanel to enforce a minimum width but allow expansion.
        JPanel cardPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                // Ensure card is at least 550px wide, but grows if text needs more space
                return new Dimension(Math.max(d.width, 550), Math.max(d.height, 500));
            }
        };

        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // --- HEADER ---
        JLabel titleLabel = new JLabel("Analysis Results");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // FIX: Use HTML to allow wrapping if screen is very narrow, ensuring no cutoff
        JLabel subtitleLabel = new JLabel("<html>Composite rankings based on your selected factors.</html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- TABLE SETUP ---
        tableModel = new DefaultTableModel(new Object[]{"Rank", "Symbol", "Score"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Double.class : String.class;
            }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(CARD_BG);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Prevent table from being squashed vertically
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // --- BUTTONS ---
        JButton backBtn = new JButton("← Adjust Configuration");
        styleLinkButton(backBtn);
        backBtn.addActionListener(e -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("configure factors");
                viewManagerModel.firePropertyChange();
            }
        });

        // --- ASSEMBLY ---
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(25));

        cardPanel.add(scrollPane);

        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(backBtn);

        add(cardPanel);

        refresh();
    }

    private void styleTable(JTable table) {
        table.setFont(TABLE_BODY_FONT);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_COLOR);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(HEADER_BG);
        header.setForeground(Color.GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Column Widths
        table.getColumnModel().getColumn(0).setMaxWidth(60); // Rank column narrow

        // Cell Rendering
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 0));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(String.format("%.4f", (Double) value));
                    setFont(TABLE_BODY_FONT.deriveFont(Font.BOLD));
                }
                return c;
            }
        };
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 15));

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(PRIMARY_COLOR);
        btn.setBackground(CARD_BG);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setForeground(PRIMARY_COLOR.darker());
            }

            public void mouseExited(MouseEvent evt) {
                btn.setForeground(PRIMARY_COLOR);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            refresh();
        }
    }

    private void refresh() {
        FactorViewState state = viewModel.getState();
        tableModel.setRowCount(0);

        int rank = 1;
        // Assuming state.getRanked() is sorted
        for (FactorViewState.RowVM row : state.getRanked()) {
            tableModel.addRow(new Object[]{
                    rank++,
                    row.symbol,
                    row.composite
            });
        }
    }

    public String getViewName() {
        return viewModel.getViewName();
    }
}