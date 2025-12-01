package use_case.import_ohlcv;

/**
 * Output boundary for the Import OHLCV use case.
 * Mirrors the pattern used in other use cases (success + fail paths).
 */
public interface ImportOHLCVOutputBoundary {

    /**
     * Called when the import succeeds.
     *
     * @param outputData summary information about the import.
     */
    void prepareSuccessView(ImportOHLCVOutputData outputData);

    /**
     * Called when the import fails (validation, repository error, etc.).
     *
     * @param errorMessage user-friendly error message.
     */
    void prepareFailView(String errorMessage);
}
