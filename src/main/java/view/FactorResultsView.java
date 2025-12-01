package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.factor_config.FactorViewModel;
import interface_adapter.factor_config.FactorViewState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View to display factor ranking results with basic styling.
 */
public class FactorResultsView extends JPanel implements PropertyChangeListener {

    private final FactorViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public FactorResultsView(FactorViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Factor Rankings", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Symbol", "Composite"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        // Zebra striping and composite right-align with 4 decimals
        DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground((row % 2 == 0) ? new Color(250, 250, 250) : Color.WHITE);
                }
                if (column == 1) {
                    setHorizontalAlignment(RIGHT);
                    if (value instanceof Double) {
                        setText(String.format("%.4f", (Double) value));
                    }
                } else {
                    setHorizontalAlignment(LEFT);
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, zebraRenderer);
        table.setDefaultRenderer(Double.class, zebraRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        add(scroll, BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("configure factors");
                viewManagerModel.firePropertyChange();
            }
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        refresh();
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
        for (FactorViewState.RowVM row : state.getRanked()) {
            tableModel.addRow(new Object[]{row.symbol, row.composite});
        }
    }

    public String getViewName() {
        return viewModel.getViewName();
    }
}
