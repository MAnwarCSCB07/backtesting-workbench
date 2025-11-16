package use_case.logout;

/**
 * The Logout Interactor.
 */
public class LogoutInteractor implements LogoutInputBoundary {
    private final LogoutUserDataAccessInterface userDataAccessObject;
    private final LogoutOutputBoundary logoutPresenter;

    public LogoutInteractor(LogoutUserDataAccessInterface userDataAccessInterface,
                            LogoutOutputBoundary logoutOutputBoundary) {
        // save the DAO and Presenter in the instance variables.
        this.userDataAccessObject = userDataAccessInterface;
        this.logoutPresenter = logoutOutputBoundary;
    }

    @Override
    public void execute() {
        // implement the logic of the Logout Use Case:
        // * read current username
        // * set current username to null in the DAO
        // * create LogoutOutputData with the (former) username
        // * tell the presenter to prepare a success view

        final String username = userDataAccessObject.getCurrentUsername();
        userDataAccessObject.setCurrentUsername(null);

        final LogoutOutputData outputData = new LogoutOutputData(username);
        logoutPresenter.prepareSuccessView(outputData);
    }
}