package data_access;

import entity.BacktestConfig;
import entity.Universe;
import entity.PriceBar;
import entity.BacktestResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backtest DAO implementation that fetches real OHLCV data from the Alpha Vantage API.
 *
 * This implementation:
 *  - Supports a simple hardcoded BacktestConfig + Universe for "demo-project"
 *  - Calls TIME_SERIES_DAILY (free endpoint) with outputsize=compact
 *  - Parses JSON with regex / string operations (no external JSON library)
 *  - Writes raw JSON response to a local file for persistence
 *
 * NOTE: You must provide a valid Alpha Vantage API key when constructing this class.
 */
public class AlphaVantageBacktestDataAccessObject implements BacktestDataAccessInterface {

    private final String apiKey;

    // configs / universes for different projects
    private final Map<String, BacktestConfig> configs = new HashMap<>();
    private final Map<String, Universe> universes = new HashMap<>();

    // cache of parsed prices
    private final Map<String, List<PriceBar>> priceCache = new HashMap<>();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AlphaVantageBacktestDataAccessObject(String apiKey) {
        this.apiKey = apiKey;

        // IMPORTANT: Alpha Vantage "compact" returns the last ~100 trading days,
        // so we choose a 2025 range that actually overlaps those dates.
        configs.put("demo-project", new BacktestConfig(
                "demo-project",
                LocalDate.of(2025, 1, 1),   // start date in 2025
                LocalDate.of(2025, 12, 31), // end date in 2025
                10000.0,
                "buy_and_hold"
        ));

        // Universe: single ticker AAPL for now
        universes.put("demo-project", new Universe(List.of("AAPL")));
    }

    // -------------------------------------------------------------------------
    // BacktestDataAccessInterface implementation
    // -------------------------------------------------------------------------

    @Override
    public BacktestConfig getConfig(String projectId) {
        return configs.get(projectId);
    }

    @Override
    public Universe getUniverse(String projectId) {
        return universes.get(projectId);
    }

    @Override
    public List<PriceBar> getPriceSeries(String ticker, LocalDate start, LocalDate end) {
        try {
            // Use cache if we already fetched this ticker once
            if (priceCache.containsKey(ticker)) {
                return filterByDateRange(priceCache.get(ticker), start, end);
            }

            String url = buildDailyUrl(ticker);
            String json = fetchJson(url);

            System.out.println("====================================");
            System.out.println("ALPHA VANTAGE RAW JSON RESPONSE:");
            System.out.println(json);
            System.out.println("====================================");

            // Persist raw JSON for this ticker
            saveRawJsonToFile(ticker, json);

            List<PriceBar> allBars = parseDailyJson(ticker, json);
            priceCache.put(ticker, allBars);

            return filterByDateRange(allBars, start, end);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveResult(BacktestResult result) {
        // Append a simple text line for each backtest result
        Path path = Paths.get("backtest_results.txt");
        String line = String.format(
                "project=%s, finalValue=%.2f, maxDrawdown=%.4f%n",
                result.getProjectId(),
                result.getFinalValue(),
                result.getMaxDrawdown()
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // HTTP + persistence helpers
    // -------------------------------------------------------------------------

    /**
     * Builds the Alpha Vantage URL for daily time series (free endpoint).
     * Example:
     * https://www.alphavantage.co/query?function=TIME_SERIES_DAILY
     *   &symbol=AAPL&outputsize=compact&apikey=YOUR_KEY
     */
    private String buildDailyUrl(String ticker) {
        return "https://www.alphavantage.co/query"
                + "?function=TIME_SERIES_DAILY"
                + "&symbol=" + ticker
                + "&outputsize=compact"
                + "&apikey=" + apiKey;
    }

    /**
     * Performs a simple HTTP GET and returns the response body as a String.
     */
    private String fetchJson(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15_000);
        connection.setReadTimeout(15_000);

        int status = connection.getResponseCode();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        status >= 200 && status < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream()
                )
        )) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Saves the raw JSON response to a local file for persistence.
     */
    private void saveRawJsonToFile(String ticker, String json) {
        try {
            Path directory = Paths.get("data", "alphavantage");
            Files.createDirectories(directory);

            Path file = directory.resolve(ticker + "_daily.json");
            Files.writeString(file, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // JSON parsing helpers
    // -------------------------------------------------------------------------

    /**
     * Parses the Alpha Vantage TIME_SERIES_DAILY JSON string into a list of PriceBar.
     * We avoid external JSON libraries by using regex and string manipulation.
     */
    private List<PriceBar> parseDailyJson(String ticker, String json) {
        List<PriceBar> bars = new ArrayList<>();

        // Find the "Time Series (Daily)" block
        String marker = "\"Time Series (Daily)\"";
        int idx = json.indexOf(marker);
        if (idx == -1) {
            // No data
            System.out.println("No 'Time Series (Daily)' block found in JSON.");
            return bars;
        }

        int startObject = json.indexOf('{', idx + marker.length());
        int endObject = findMatchingBrace(json, startObject);
        if (startObject == -1 || endObject == -1) {
            System.out.println("Could not locate braces for time series block.");
            return bars;
        }

        String timeSeriesBlock = json.substring(startObject, endObject + 1);

        // Regex to capture each date entry:  "YYYY-MM-DD" : { ... }
        Pattern entryPattern =
                Pattern.compile("\"(\\d{4}-\\d{2}-\\d{2})\"\\s*:\\s*\\{([^}]*)\\}");
        Matcher entryMatcher = entryPattern.matcher(timeSeriesBlock);

        while (entryMatcher.find()) {
            String dateStr = entryMatcher.group(1);
            String fieldsBlock = entryMatcher.group(2);

            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);

            double open = parseField(fieldsBlock, "\"1. open\"");
            double high = parseField(fieldsBlock, "\"2. high\"");
            double low = parseField(fieldsBlock, "\"3. low\"");
            double close = parseField(fieldsBlock, "\"4. close\"");
            // Daily endpoint uses "5. volume"
            long volume = (long) parseField(fieldsBlock, "\"5. volume\"");

            PriceBar bar = new PriceBar(
                    ticker,
                    date,
                    open,
                    high,
                    low,
                    close,
                    volume
            );
            bars.add(bar);
        }

        // Data comes newest → oldest; sort ascending by date
        bars.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        return bars;
    }

    /**
     * Very small helper to parse a numeric field from a JSON object block.
     * Expects formats like:  "1. open": "123.4500"
     */
    private double parseField(String block, String fieldName) {
        int idx = block.indexOf(fieldName);
        if (idx == -1) {
            return 0.0;
        }

        int colonIdx = block.indexOf(':', idx);
        if (colonIdx == -1) {
            return 0.0;
        }

        int firstQuote = block.indexOf('"', colonIdx + 1);
        int secondQuote = block.indexOf('"', firstQuote + 1);

        if (firstQuote == -1 || secondQuote == -1) {
            return 0.0;
        }

        String valueStr = block.substring(firstQuote + 1, secondQuote);
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Utility: given a string and an index to '{', find the matching '}' index.
     */
    private int findMatchingBrace(String s, int openIndex) {
        if (openIndex < 0 || openIndex >= s.length() || s.charAt(openIndex) != '{') {
            return -1;
        }
        int depth = 0;
        for (int i = openIndex; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;

            if (depth == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Filters a list of PriceBar to the given [start, end] date range (inclusive).
     */
    private List<PriceBar> filterByDateRange(List<PriceBar> allBars,
                                             LocalDate start,
                                             LocalDate end) {
        List<PriceBar> result = new ArrayList<>();
        for (PriceBar bar : allBars) {
            LocalDate d = bar.getDate();
            if ((start == null || !d.isBefore(start)) &&
                    (end == null || !d.isAfter(end))) {
                result.add(bar);
            }
        }
        return result;
    }
}