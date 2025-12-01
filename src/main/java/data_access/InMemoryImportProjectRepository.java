package data_access;

import entity.PriceBar;
import use_case.import_ohlcv.ImportOHLCVProjectRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Minimal in-memory implementation of ImportOHLCVProjectRepository.
 *
 * - Knows which project IDs "exist".
 * - Records last imported date range and tickers per project (in memory only).
 *
 * This keeps UC-1 clean and testable without forcing a full persistence layer.
 */
public class InMemoryImportProjectRepository implements ImportOHLCVProjectRepository {

    private final Set<String> existingProjects = new HashSet<>();
    private final Map<String, ImportRecord> importsByProject = new HashMap<>();

    public InMemoryImportProjectRepository() {
        // Pre-register a demo project; you can add more if needed
        existingProjects.add("demo-project");
    }

    @Override
    public boolean projectExists(String projectId) {
        return existingProjects.contains(projectId);
    }

    @Override
    public void saveImportedPrices(
            String projectId,
            Map<String, List<PriceBar>> pricesByTicker,
            LocalDate start,
            LocalDate end
    ) {
        existingProjects.add(projectId);
        importsByProject.put(projectId,
                new ImportRecord(projectId, start, end, pricesByTicker));
        // TODO: later, bridge this to a real Project/Backtest repository if desired.
    }

    private static class ImportRecord {
        final String projectId;
        final LocalDate start;
        final LocalDate end;
        final Map<String, List<PriceBar>> pricesByTicker;

        ImportRecord(String projectId,
                     LocalDate start,
                     LocalDate end,
                     Map<String, List<PriceBar>> pricesByTicker) {
            this.projectId = projectId;
            this.start = start;
            this.end = end;
            this.pricesByTicker = pricesByTicker;
        }
    }
}
