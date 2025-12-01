package app;

import data_access.FileExportGatewayImpl;
import data_access.FileProjectRepository;
import data_access.FileUserDataAccessObject;
import entity.BacktestConfig;
import entity.BacktestResult;
import entity.Project;
import entity.Universe;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.save_export.SaveExportController;
import interface_adapter.save_export.SaveExportPresenter;
import interface_adapter.save_export.SaveExportViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.save_export.FileExportGateway;
import use_case.save_export.SaveExportInputBoundary;
import use_case.save_export.SaveExportInteractor;
import use_case.save_export.SaveExportOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import view.*;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using local file storage
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);

    // DAO version using a shared external database
    // final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory);

    // Shared project repository for Save/Export use case
    final FileProjectRepository projectRepository = new FileProjectRepository();

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private ChartsView chartsView;
    private AlphaVantageView alphaVantageView;
    private InputStockDataView inputStockDataView;
    private ConfigureFactorsView configureFactorsView;
    private SaveExportView saveExportView;
    private SaveExportViewModel saveExportViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addChartsView() {
        chartsView = new ChartsView();
        cardPanel.add(chartsView, chartsView.viewName);
        return this;
    }

    public AppBuilder addAlphaVantageView() {
        alphaVantageView = new AlphaVantageView(viewManagerModel);
        cardPanel.add(alphaVantageView, alphaVantageView.viewName);
        return this;
    }

    public AppBuilder addInputStockDataView() {
        inputStockDataView = new InputStockDataView(viewManagerModel);
        cardPanel.add(inputStockDataView, inputStockDataView.viewName);
        return this;
    }

    public AppBuilder addConfigureFactorsView() {
        configureFactorsView = new ConfigureFactorsView(viewManagerModel);
        cardPanel.add(configureFactorsView, configureFactorsView.viewName);
        return this;
    }

    public AppBuilder addSaveExportView() {
        saveExportViewModel = new SaveExportViewModel();
        saveExportView = new SaveExportView(saveExportViewModel);
        cardPanel.add(saveExportView, saveExportView.getViewName());
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    /**
     * Adds the Save/Export Use Case to the application.
     * @return this builder
     */
    public AppBuilder addSaveExportUseCase() {
        // Use shared repository instance and create gateway implementation
        final FileExportGateway fileExportGateway = new FileExportGatewayImpl();

        // Create presenter
        final SaveExportOutputBoundary saveExportOutputBoundary = new SaveExportPresenter(saveExportViewModel);

        // Create interactor
        final SaveExportInputBoundary saveExportInteractor =
                new SaveExportInteractor(projectRepository, fileExportGateway, saveExportOutputBoundary);

        // Create controller and wire to view
        final SaveExportController saveExportController = new SaveExportController(saveExportInteractor);
        saveExportView.setSaveExportController(saveExportController);

        // Create a test project for demonstration purposes
        // TODO: Remove this when UC-2 and UC-3 create projects
        createTestProject();

        return this;
    }

    /**
     * Creates a test project for demonstration purposes.
     * This should be replaced when UC-2 and UC-3 are integrated.
     */
    private void createTestProject() {
        // Create a sample universe
        java.util.List<String> tickers = java.util.Arrays.asList("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA");
        Universe universe = new Universe(tickers);

        // Create sample factor weights
        java.util.Map<String, Double> factorWeights = new java.util.HashMap<>();
        factorWeights.put("momentum", 0.4);
        factorWeights.put("value", 0.3);
        factorWeights.put("low_vol", 0.2);
        factorWeights.put("quality", 0.1);
        BacktestConfig config = new BacktestConfig("monthly", 10.0, 0.1, factorWeights);

        // Create sample backtest results
        java.util.Map<String, Double> metrics = new java.util.HashMap<>();
        metrics.put("Sharpe Ratio", 1.52);
        metrics.put("CAGR", 0.125);
        metrics.put("Max Drawdown", -0.18);
        metrics.put("Win Rate", 0.58);
        java.util.List<Double> equityCurve = java.util.Arrays.asList(
            100.0, 102.5, 105.0, 103.0, 108.0, 110.0, 107.0, 112.0, 115.0, 113.0
        );
        java.util.List<Double> drawdown = java.util.Arrays.asList(
            0.0, -0.02, -0.05, -0.07, -0.10, -0.12, -0.15, -0.18, -0.15, -0.12
        );
        BacktestResult result = new BacktestResult(equityCurve, drawdown, metrics);

        // Create and save the test project
        Project testProject = new Project("demo-project-1", "Demo Backtest Project", universe, config, result);
        projectRepository.save(testProject);
    }

    public JFrame build() {
        final JFrame application = new JFrame("User Login Example");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }


}
