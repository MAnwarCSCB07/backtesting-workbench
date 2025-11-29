package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;

public class AlphaVantageView extends JPanel {
    public final String viewName = "alpha vantage";

    public AlphaVantageView(ViewManagerModel viewManagerModel) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);

        JLabel title = new JLabel("Alpha Vantage Page");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(title);

        this.add(Box.createVerticalStrut(40));

        JButton configureFactors = new JButton("Configure Factors");
        configureFactors.setAlignmentX(Component.CENTER_ALIGNMENT);
        configureFactors.addActionListener(e -> {
            viewManagerModel.setState("configure factors");
            viewManagerModel.firePropertyChange();
        });
        this.add(configureFactors);

        this.add(Box.createVerticalStrut(20));

        JButton back = new JButton("Return");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });
        this.add(back);
    }
}
