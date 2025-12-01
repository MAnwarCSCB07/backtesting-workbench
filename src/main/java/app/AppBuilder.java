package app;

import data_access.FileUserDataAccessObject;
import data_access.BacktestDataAccessInterface;
import data_access.InMemoryBacktestDataAccessObject;

import entity.UserFactory;
import data_access.AlphaVantageBacktestDataAccessObject;
import interface_adapter.ViewManagerModel;

import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;

import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.factor_config.FactorConfigController;
import interface_adapter.factor_config.FactorConfigPresenter;
import interface_adapter.factor_config.FactorViewModel;
import data_access.InMemoryFactorDataGateway;
import use_case.factor_config.FactorConfigInputBoundary;
import use_case.factor_config.FactorConfigInteractor;
import use_case.factor_config.FactorConfigOutputBoundary;

import interface_adapter.run_backtest.RunBacktestController;
import interface_adapter.run_backtest.RunBacktestPresenter;
import interface_adapter.run_backtest.RunBacktestViewModel;

import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;

import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;

import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;

import use_case.run_backtest.RunBacktestInputBoundary;
import use_case.run_backtest.RunBacktestInteractor;
import use_case.run_backtest.RunBacktestOutputBoundary;

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
    private FactorResultsView factorResultsView;
    private FactorViewModel factorViewModel;

    private RunBacktestViewModel runBacktestViewModel;
    private RunBacktestView runBacktestView;

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

    public AppBuilder addRunBacktestView() {
        runBacktestViewModel = new RunBacktestViewModel();
        runBacktestView = new RunBacktestView(runBacktestViewModel);
        cardPanel.add(runBacktestView, runBacktestView.getViewName());
        return this;
    }

    public AppBuilder addFactorResultsView() {
        factorViewModel = new FactorViewModel();
        factorResultsView = new FactorResultsView(factorViewModel, viewManagerModel);
        cardPanel.add(factorResultsView, factorResultsView.getViewName());
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(
                viewManagerModel, signupViewModel, loginViewModel);

        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(
                viewManagerModel, loggedInViewModel, loginViewModel);

        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary =
                new ChangePasswordPresenter(viewManagerModel, loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController =
                new ChangePasswordController(changePasswordInteractor);

        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    public AppBuilder addFactorConfigUseCase() {
        // Presenter updates the FactorViewModel and navigates to results view
        final FactorConfigOutputBoundary presenter = new FactorConfigPresenter(factorViewModel, viewManagerModel);
        // TODO [UC-2 Integration]: Replace InMemoryFactorDataGateway with a real AlphaVantage-backed implementation
        //  - Create a class like AlphaVantageFactorDataGateway implements FactorDataGateway (in data_access package)
        //  - It should fetch inputs required by entity-level calculators (e.g., momentum12m1, volatility)
        //  - Inject API key/config as needed (do NOT hardcode here). Consider using env vars or a config provider.
        //  - Then pass that implementation below instead of the in-memory stub.
        final FactorConfigInputBoundary interactor = new FactorConfigInteractor(presenter, new InMemoryFactorDataGateway());
        final FactorConfigController controller = new FactorConfigController(interactor);
        configureFactorsView.setFactorConfigController(controller);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     *
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(
                viewManagerModel, loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    public AppBuilder addRunBacktestUseCase() {
        final BacktestDataAccessInterface backtestDAO =
                new AlphaVantageBacktestDataAccessObject("XRI7X6PMTUFWUH1E");

        final RunBacktestOutputBoundary outputBoundary =
                new RunBacktestPresenter(runBacktestViewModel);

        final RunBacktestInputBoundary interactor =
                new RunBacktestInteractor(backtestDAO, outputBoundary);

        final RunBacktestController controller =
                new RunBacktestController(interactor);

        runBacktestView.setRunBacktestController(controller);

        return this;
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