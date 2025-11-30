package interface_adapter.run_backtest;

import interface_adapter.ViewModel;

public class RunBacktestViewModel extends ViewModel<RunBacktestState> {

    public static final String VIEW_NAME = "run backtest";

    public RunBacktestViewModel() {
        super(VIEW_NAME);
        // initialize default state
        this.setState(new RunBacktestState());
    }

    public RunBacktestState getState() {
        return this.getStateInternal();
    }

    public void setState(RunBacktestState state) {
        this.setStateInternal(state);
    }

    public void fireStateChanged() {
        // use the method that actually exists in ViewModel
        firePropertyChange();
    }

    // Helper methods to avoid raw-type warnings; you could also just call getState()/setState directly.
    private RunBacktestState getStateInternal() {
        return super.getState();
    }

    private void setStateInternal(RunBacktestState state) {
        super.setState(state);
    }
}