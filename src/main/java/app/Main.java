package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look & feel for a more native, polished UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        System.out.println("Main started");
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addSignupView()
                .addLoginView()
                .addLoggedInView()
                .addAlphaVantageView()
                .addInputStockDataView()
                .addConfigureFactorsView()
                .addFactorResultsView()
                .addChartsView()
                .addRunBacktestView()
                .addSaveExportView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addRunBacktestUseCase()
                .addImportOHLCVUseCase()
                .addFactorConfigUseCase()
                .addSaveExportUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
