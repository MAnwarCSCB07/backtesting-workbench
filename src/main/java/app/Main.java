package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main started");
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addSignupView()
                .addLoginView()
                .addLoggedInView()
                .addAlphaVantageView()
                .addInputStockDataView()
                .addConfigureFactorsView()
                .addChartsView()
                .addRunBacktestView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addRunBacktestUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}