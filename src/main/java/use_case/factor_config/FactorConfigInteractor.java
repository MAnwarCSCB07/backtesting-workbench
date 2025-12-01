package use_case.factor_config;

import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;
import entity.factors.FactorCalculator;
import entity.factors.FactorDataGateway;
import entity.factors.LowVolFactor;
import entity.factors.MomentumFactor;
import entity.factors.ReversalFactor;
import entity.factors.SizeFactor;
import entity.factors.ValueProxyFactor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interactor for UC-2: Configure Factors & Rank.
 */
public class FactorConfigInteractor implements FactorConfigInputBoundary {

    private final FactorConfigOutputBoundary presenter;
    private final FactorDataGateway dataGateway;
    private final Map<Factor, FactorCalculator> calculators;
    private static final Logger LOG = Logger.getLogger(FactorConfigInteractor.class.getName());

    public FactorConfigInteractor(FactorConfigOutputBoundary presenter,
                                  FactorDataGateway dataGateway,
                                  Map<Factor, FactorCalculator> calculators) {
        this.presenter = Objects.requireNonNull(presenter);
        this.dataGateway = Objects.requireNonNull(dataGateway);
        this.calculators = new EnumMap<>(Objects.requireNonNull(calculators));
    }

    /**
     * Convenience constructor with default calculators for Momentum and Low Volatility.
     */
    public FactorConfigInteractor(FactorConfigOutputBoundary presenter,
                                  FactorDataGateway dataGateway) {
        this.presenter = presenter;
        this.dataGateway = dataGateway;
        this.calculators = new EnumMap<>(Factor.class);
        this.calculators.put(Factor.MOMENTUM_12_1, new MomentumFactor());
        this.calculators.put(Factor.LOW_VOL, new LowVolFactor());
        this.calculators.put(Factor.REVERSAL_1_1, new ReversalFactor());
        this.calculators.put(Factor.SIZE, new SizeFactor());
        this.calculators.put(Factor.VALUE_PROXY, new ValueProxyFactor());
    }

    @Override
    public void execute(FactorConfigInputData inputData) {
        final List<String> symbols = inputData.getSymbols();
        final List<Factor> selected = inputData.getSelectedFactors();
        final Map<Factor, Double> weights = inputData.getWeights();
        final PreprocessingMethod preprocessing = inputData.getPreprocessing();

        // Debug-level input echo; keep summary results at INFO below
        LOG.log(Level.FINE, "[UC-2] Execute symbols=" + symbols
                + ", factors=" + selected
                + ", weights=" + weights
                + ", prep=" + preprocessing);

        // 1) Compute raw per-factor scores
        Map<Factor, Map<String, Double>> rawByFactor = new EnumMap<>(Factor.class);
        for (Factor f : selected) {
            FactorCalculator calc = calculators.get(f);
            if (calc == null) {
                // Skip unknown factors gracefully
                LOG.log(Level.WARNING, "[UC-2] No calculator for factor=" + f + "; skipping");
                continue;
            }
            Map<String, Double> scores = calc.compute(symbols, dataGateway);
            LOG.log(Level.FINE, "[UC-2] Computed raw scores for " + f + " (" + scores.size() + ")");
            rawByFactor.put(f, scores);
        }

        // 2) Preprocess per-factor if requested
        Map<Factor, Map<String, Double>> usedByFactor = new EnumMap<>(Factor.class);
        for (Map.Entry<Factor, Map<String, Double>> e : rawByFactor.entrySet()) {
            Map<String, Double> values = e.getValue();
            Map<String, Double> processed;
            if (preprocessing == PreprocessingMethod.Z_SCORE) {
                processed = zScore(values);
                double mean = values.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double variance = values.values().stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0.0);
                double std = Math.sqrt(variance);
                LOG.log(Level.FINE, "[UC-2] Z-scored factor=" + e.getKey() + ", mean=" + mean + ", std=" + std);
            } else if (preprocessing == PreprocessingMethod.WINSORIZE) {
                processed = winsorize(values, 0.05, 0.95);
                LOG.log(Level.FINE, "[UC-2] Winsorized factor=" + e.getKey());
            } else {
                processed = values;
            }
            usedByFactor.put(e.getKey(), processed);
        }

        // 3) Combine by weighted average
        Map<String, Double> compositeBySymbol = new HashMap<>();
        for (String sym : symbols) {
            double sum = 0.0;
            for (Factor f : selected) {
                double w = weights.getOrDefault(f, 0.0);
                Map<String, Double> map = usedByFactor.get(f);
                if (map == null) continue; // unknown factor skipped
                double val = map.getOrDefault(sym, 0.0);
                sum += w * val;
            }
            compositeBySymbol.put(sym, sum);
        }

        // 4) Build output rows and sort desc by composite
        List<FactorConfigOutputData.Row> rows = new ArrayList<>();
        for (String sym : symbols) {
            Map<String, Double> zByKey = new HashMap<>();
            for (Factor f : selected) {
                Map<String, Double> map = usedByFactor.get(f);
                if (map == null) continue;
                zByKey.put(f.name(), map.getOrDefault(sym, 0.0));
            }
            rows.add(new FactorConfigOutputData.Row(
                    sym,
                    compositeBySymbol.getOrDefault(sym, 0.0),
                    zByKey
            ));
        }

        rows = rows.stream()
                .sorted(Comparator.comparingDouble((FactorConfigOutputData.Row r) -> r.composite).reversed())
                .collect(Collectors.toList());

        if (!rows.isEmpty()) {
            int n = Math.min(5, rows.size());
            String top = rows.subList(0, n).stream()
                    .map(r -> r.symbol + ":" + String.format(java.util.Locale.US, "%.4f", r.composite))
                    .collect(Collectors.joining(", "));
            LOG.log(Level.INFO, "[UC-2] Ranked " + rows.size() + " symbols; top " + n + ": " + top);
        } else {
            LOG.log(Level.WARNING, "[UC-2] No rows produced in factor ranking");
        }

        presenter.present(new FactorConfigOutputData(rows));
    }

    private static Map<String, Double> zScore(Map<String, Double> values) {
        double mean = values.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.values().stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Map<String, Double> out = new HashMap<>();
        if (std == 0.0) {
            for (Map.Entry<String, Double> e : values.entrySet()) out.put(e.getKey(), 0.0);
        } else {
            for (Map.Entry<String, Double> e : values.entrySet()) out.put(e.getKey(), (e.getValue() - mean) / std);
        }
        return out;
    }

    private static Map<String, Double> winsorize(Map<String, Double> values, double lowerProb, double upperProb) {
        List<Double> sorted = new ArrayList<>(values.values());
        sorted.sort(Double::compareTo);
        if (sorted.isEmpty()) return new HashMap<>();

        lowerProb = Math.max(0.0, Math.min(lowerProb, 1.0));
        upperProb = Math.max(0.0, Math.min(upperProb, 1.0));
        if (upperProb < lowerProb) {
            double tmp = lowerProb; lowerProb = upperProb; upperProb = tmp;
        }

        int n = sorted.size();
        int li = (int) Math.floor(lowerProb * (n - 1));
        int ui = (int) Math.floor(upperProb * (n - 1));
        double lo = sorted.get(li);
        double hi = sorted.get(ui);

        Map<String, Double> out = new HashMap<>();
        for (Map.Entry<String, Double> e : values.entrySet()) {
            double v = e.getValue();
            if (v < lo) v = lo;
            if (v > hi) v = hi;
            out.put(e.getKey(), v);
        }
        return out;
    }
}
