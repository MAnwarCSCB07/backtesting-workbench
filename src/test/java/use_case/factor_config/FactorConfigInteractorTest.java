package use_case.factor_config;

import data_access.InMemoryFactorDataGateway;
import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;
import entity.factors.FactorCalculator;
import entity.factors.MomentumFactor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class FactorConfigInteractorTest {

    @Test
    void zScoreWeightedRankingTest() {
        // Symbols
        List<String> symbols = List.of("AAA", "BBB", "CCC");

        // Data gateway with deterministic values
        InMemoryFactorDataGateway gateway = new InMemoryFactorDataGateway()
                .withMomentum("AAA", 10)
                .withMomentum("BBB", 20)
                .withMomentum("CCC", 30)
                .withVolatility("AAA", 30) // lower vol is better (inverted in LowVolFactor)
                .withVolatility("BBB", 20)
                .withVolatility("CCC", 10);

        // Selected factors and weights
        List<Factor> selected = List.of(Factor.MOMENTUM_12_1, Factor.LOW_VOL);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 0.6);
        weights.put(Factor.LOW_VOL, 0.4);

        FactorConfigInputData input = new FactorConfigInputData(
                symbols, selected, weights, PreprocessingMethod.Z_SCORE);

        // Capture presenter output
        final AtomicReference<FactorConfigOutputData> captured = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = captured::set;

        FactorConfigInteractor interactor = new FactorConfigInteractor(presenter, gateway);
        interactor.execute(input);

        FactorConfigOutputData out = captured.get();
        assertNotNull(out);
        assertEquals(3, out.getRanked().size());

        // Because both factors are strictly increasing for CCC and decreasing vol inverted,
        // CCC should rank first, AAA should rank last.
        assertEquals("CCC", out.getRanked().get(0).symbol);
        assertEquals("AAA", out.getRanked().get(2).symbol);

        // Verify z-scores roughly (momentum mean=20, std≈8.16497 => z: AAA≈-1.225, BBB≈0, CCC≈1.225)
        FactorConfigOutputData.Row rowAAA = out.getRanked().stream().filter(r -> r.symbol.equals("AAA")).findFirst().get();
        double zMomAAA = rowAAA.zScoresByFactor.get("MOMENTUM_12_1");
        assertTrue(zMomAAA < 0);

        // Low vol: raw vol {30,20,10} inverted => {-30,-20,-10}; mean=-20, std≈8.16497 => z AAA≈-1.225
        double zLowVolAAA = rowAAA.zScoresByFactor.get("LOW_VOL");
        assertTrue(zLowVolAAA < 0);

        // Composite ordering check: strictly increasing across AAA->BBB->CCC
        double compAAA = rowAAA.composite;
        double compBBB = out.getRanked().stream().filter(r -> r.symbol.equals("BBB")).findFirst().get().composite;
        double compCCC = out.getRanked().stream().filter(r -> r.symbol.equals("CCC")).findFirst().get().composite;
        assertTrue(compAAA < compBBB);
        assertTrue(compBBB < compCCC);
    }

    @Test
    void sizeAndValueProxyRankingTest() {
        // Symbols
        List<String> symbols = List.of("AAA", "BBB", "CCC");

        // In-memory gateway with deterministic fundamentals
        InMemoryFactorDataGateway gateway = new InMemoryFactorDataGateway()
                // Market caps (smaller is better after -log): CCC < BBB < AAA
                .withSize("AAA", 100_000_000_000.0)
                .withSize("BBB", 50_000_000_000.0)
                .withSize("CCC", 10_000_000_000.0)
                // Value proxy (higher is better): CCC highest
                .withValueProxy("AAA", 0.05)
                .withValueProxy("BBB", 0.04)
                .withValueProxy("CCC", 0.10);

        // Selected factors and equal weights
        List<Factor> selected = List.of(Factor.SIZE, Factor.VALUE_PROXY);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.SIZE, 0.5);
        weights.put(Factor.VALUE_PROXY, 0.5);

        FactorConfigInputData input = new FactorConfigInputData(
                symbols, selected, weights, PreprocessingMethod.Z_SCORE);

        final java.util.concurrent.atomic.AtomicReference<FactorConfigOutputData> captured = new java.util.concurrent.atomic.AtomicReference<>();
        FactorConfigOutputBoundary presenter = captured::set;

        FactorConfigInteractor interactor = new FactorConfigInteractor(presenter, gateway);
        interactor.execute(input);

        FactorConfigOutputData out = captured.get();
        assertNotNull(out);
        assertEquals(3, out.getRanked().size());

        // CCC should be best (smallest size and highest value proxy)
        assertEquals("CCC", out.getRanked().get(0).symbol);
        // AAA should be worst (largest size and lowest value proxy among the three)
        assertEquals("AAA", out.getRanked().get(2).symbol);

        // Verify that SIZE z-score for AAA is worst (since -log(size) makes big caps small/negative)
        FactorConfigOutputData.Row rowAAA = out.getRanked().stream().filter(r -> r.symbol.equals("AAA")).findFirst().get();
        double zSizeAAA = rowAAA.zScoresByFactor.get("SIZE");
        assertTrue(zSizeAAA < 0);
    }

    // ---- Consolidated coverage tests from FactorConfigInteractorCoverageTest ----

    @Test
    void preprocessingNone_passesThroughRawValues() {
        List<String> symbols = List.of("A", "B");
        InMemoryFactorDataGateway gw = new InMemoryFactorDataGateway()
                .withMomentum("A", 10)
                .withMomentum("B", 30);

        List<Factor> selected = List.of(Factor.MOMENTUM_12_1);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 1.0);

        FactorConfigInputData in = new FactorConfigInputData(symbols, selected, weights, PreprocessingMethod.NONE);

        AtomicReference<FactorConfigOutputData> cap = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = cap::set;
        new FactorConfigInteractor(presenter, gw).execute(in);

        FactorConfigOutputData out = cap.get();
        assertNotNull(out);
        Map<String, Double> bySym = new HashMap<>();
        out.getRanked().forEach(r -> bySym.put(r.symbol, r.composite));
        assertEquals(10.0, bySym.get("A"));
        assertEquals(30.0, bySym.get("B"));
        assertEquals("B", out.getRanked().get(0).symbol);
    }

    @Test
    void missingCalculator_isSkippedGracefully() {
        List<String> symbols = List.of("A", "B");
        InMemoryFactorDataGateway gw = new InMemoryFactorDataGateway()
                .withMomentum("A", 1)
                .withMomentum("B", 2);

        Map<Factor, FactorCalculator> calcs = new EnumMap<>(Factor.class);
        calcs.put(Factor.MOMENTUM_12_1, new MomentumFactor());

        List<Factor> selected = List.of(Factor.MOMENTUM_12_1, Factor.LOW_VOL);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 1.0);

        FactorConfigInputData in = new FactorConfigInputData(symbols, selected, weights, PreprocessingMethod.Z_SCORE);

        AtomicReference<FactorConfigOutputData> cap = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = cap::set;
        new FactorConfigInteractor(presenter, gw, calcs).execute(in);

        FactorConfigOutputData out = cap.get();
        assertNotNull(out);
        assertEquals(2, out.getRanked().size());
        assertEquals("B", out.getRanked().get(0).symbol);
        assertEquals("A", out.getRanked().get(1).symbol);
    }

    @Test
    void zScore_zeroStd_resultsInZeros() {
        List<String> symbols = List.of("A", "B", "C");
        InMemoryFactorDataGateway gw = new InMemoryFactorDataGateway()
                .withMomentum("A", 5)
                .withMomentum("B", 5)
                .withMomentum("C", 5);

        List<Factor> selected = List.of(Factor.MOMENTUM_12_1);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 1.0);

        FactorConfigInputData in = new FactorConfigInputData(symbols, selected, weights, PreprocessingMethod.Z_SCORE);
        AtomicReference<FactorConfigOutputData> cap = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = cap::set;
        new FactorConfigInteractor(presenter, gw).execute(in);

        FactorConfigOutputData out = cap.get();
        assertNotNull(out);
        for (FactorConfigOutputData.Row r : out.getRanked()) {
            assertEquals(0.0, r.zScoresByFactor.get("MOMENTUM_12_1"));
            assertEquals(0.0, r.composite);
        }
    }

    @Test
    void emptySymbols_producesEmptyRows() {
        List<String> symbols = new ArrayList<>();
        InMemoryFactorDataGateway gw = new InMemoryFactorDataGateway();
        List<Factor> selected = List.of(Factor.MOMENTUM_12_1);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 1.0);

        FactorConfigInputData in = new FactorConfigInputData(symbols, selected, weights, PreprocessingMethod.Z_SCORE);
        AtomicReference<FactorConfigOutputData> cap = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = cap::set;
        new FactorConfigInteractor(presenter, gw).execute(in);

        FactorConfigOutputData out = cap.get();
        assertNotNull(out);
        assertTrue(out.getRanked().isEmpty());
    }

    @Test
    void weightsDefaultZero_unweightedFactorDoesNotAffectComposite() {
        List<String> symbols = List.of("A", "B");
        InMemoryFactorDataGateway gw = new InMemoryFactorDataGateway()
                .withMomentum("A", 10)
                .withMomentum("B", 20)
                .withVolatility("A", 100)
                .withVolatility("B", 1);

        List<Factor> selected = List.of(Factor.MOMENTUM_12_1, Factor.LOW_VOL);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 1.0);

        FactorConfigInputData in = new FactorConfigInputData(symbols, selected, weights, PreprocessingMethod.NONE);
        AtomicReference<FactorConfigOutputData> cap = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = cap::set;
        new FactorConfigInteractor(presenter, gw).execute(in);

        FactorConfigOutputData out = cap.get();
        assertNotNull(out);
        Map<String, Double> comp = new HashMap<>();
        out.getRanked().forEach(r -> comp.put(r.symbol, r.composite));
        assertEquals(10.0, comp.get("A"));
        assertEquals(20.0, comp.get("B"));
        assertEquals("B", out.getRanked().get(0).symbol);
    }

    @Test
    void winsorize_reflectionCoversSwapAndEmptyBranches() throws Exception {
        Method m = FactorConfigInteractor.class.getDeclaredMethod("winsorize", Map.class, double.class, double.class);
        m.setAccessible(true);

        Map<String, Double> empty = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, Double> resEmpty = (Map<String, Double>) invoke(m, null, empty, 0.05, 0.95);
        assertTrue(resEmpty.isEmpty());

        Map<String, Double> vals = new HashMap<>();
        vals.put("a", -100.0);
        vals.put("b", 0.0);
        vals.put("c", 100.0);
        @SuppressWarnings("unchecked")
        Map<String, Double> res = (Map<String, Double>) invoke(m, null, vals, 0.90, 0.10);
        assertEquals(-100.0, res.get("a"));
        assertEquals(0.0, res.get("b"));
        assertEquals(0.0, res.get("c"));
    }

    @Test
    void execute_winsorizePathIsExecuted() {
        List<String> symbols = List.of("A", "B", "C");
        InMemoryFactorDataGateway gw = new InMemoryFactorDataGateway()
                .withMomentum("A", -100)
                .withMomentum("B", 0)
                .withMomentum("C", 100);

        List<Factor> selected = List.of(Factor.MOMENTUM_12_1);
        Map<Factor, Double> weights = new EnumMap<>(Factor.class);
        weights.put(Factor.MOMENTUM_12_1, 1.0);

        FactorConfigInputData in = new FactorConfigInputData(symbols, selected, weights, PreprocessingMethod.WINSORIZE);
        AtomicReference<FactorConfigOutputData> cap = new AtomicReference<>();
        FactorConfigOutputBoundary presenter = cap::set;
        new FactorConfigInteractor(presenter, gw).execute(in);

        FactorConfigOutputData out = cap.get();
        assertNotNull(out);
        assertEquals(3, out.getRanked().size());
        assertEquals("A", out.getRanked().get(2).symbol);
        assertEquals(0.0, out.getRanked().get(0).composite);
    }

    // Reflection helper copied from the coverage test
    private static Object invoke(Method m, Object target, Object... args) throws Exception {
        try {
            return m.invoke(target, args);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) throw (Exception) cause;
            throw e;
        }
    }
}
