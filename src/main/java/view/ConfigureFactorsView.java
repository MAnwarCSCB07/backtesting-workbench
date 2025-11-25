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
