package entity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import java.io.Serializable;

public class BacktestConfig {

    private final String projectId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double initialCapital;
    private final String strategyName;

    public BacktestConfig(String projectId,
                          LocalDate startDate,
                          LocalDate endDate,
                          double initialCapital,
                          String strategyName) {
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.strategyName = strategyName;
    }

    public String getProjectId() {
        return projectId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getInitialCapital() {
        return initialCapital;
    }

    public String getStrategyName() {
        return strategyName;
    }

    // Enums

    public enum Factor implements Serializable {
        MOMENTUM_12_1,
        REVERSAL_1_1,
        SIZE,
        VALUE_PROXY,
        LOW_VOL
    }

    public enum PreprocessingMethod {
        WINSORIZE,
        Z_SCORE,
        NONE
    }
}