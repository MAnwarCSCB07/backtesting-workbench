package interface_adapter.factor_config;

import interface_adapter.ViewModel;

public class FactorViewModel extends ViewModel<FactorViewState> {
    public FactorViewModel() {
        super("factor results");
        this.setState(new FactorViewState());
    }
}
