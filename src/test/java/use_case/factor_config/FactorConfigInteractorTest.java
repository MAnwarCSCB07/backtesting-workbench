package use_case.factor_config;

import data_access.InMemoryFactorDataGateway;
import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
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
}
