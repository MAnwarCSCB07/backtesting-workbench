package interface_adapter.factor_config;

import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;
import use_case.factor_config.FactorConfigInputBoundary;
import use_case.factor_config.FactorConfigInputData;

import java.util.List;
import java.util.Map;

public class FactorConfigController {
    private final FactorConfigInputBoundary interactor;

    public FactorConfigController(FactorConfigInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(List<String> symbols,
                        List<Factor> selectedFactors,
                        Map<Factor, Double> weights,
                        PreprocessingMethod preprocessing) {
        interactor.execute(new FactorConfigInputData(symbols, selectedFactors, weights, preprocessing));
    }
}
