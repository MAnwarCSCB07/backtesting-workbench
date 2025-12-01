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
}
