package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look & feel for a more native, polished UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addAlphaVantageView()
                .addInputStockDataView()
                .addConfigureFactorsView()
                .addFactorResultsView()
                .addChartsView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addFactorConfigUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
