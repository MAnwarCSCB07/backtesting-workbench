package data_access;

import entity.factors.FactorDataGateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;

import org.json.JSONObject;

public class AlphaVantageFactorDataGateway implements FactorDataGateway {

    private final String apiKey;
    private final Map<String, NavigableMap<LocalDate, Double>> closeCache = new TreeMap<>();
    private final Map<String, Long> marketCapCache = new TreeMap<>();
    private final Map<String, Double> valueProxyCache = new TreeMap<>();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
    private static final Logger LOG = Logger.getLogger(AlphaVantageFactorDataGateway.class.getName());

    public AlphaVantageFactorDataGateway(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public double momentum12m1(String symbol) {
        List<Double> closes = getAscendingCloses(symbol);
        if (closes.size() < 64) {
            LOG.log(Level.FINE, "[Factors][" + symbol + "] insufficient history for 12-1 momentum");
            return 0.0;
        }
        double p_recent = closes.get(closes.size() - 22);
        double p_past = closes.get(closes.size() - 64);

        if (p_recent <= 0 || p_past <= 0) return 0.0;
        double val = (p_recent / p_past) - 1.0;
        LOG.log(Level.FINE, String.format(Locale.US, "[Factors][%s] Momentum(12-1)=%.6f", symbol, val));
        return val;
    }

    /**
     * REVERSAL (1_1): Short term reversal.
     * Calculated as the return of the last 1 month (21 trading days).
     * Formula: (P(t) / P(t-21)) - 1
     */
    @Override
    public double reversal(String symbol) {
        List<Double> closes = getAscendingCloses(symbol);

        // We need at least 22 days of history
        if (closes.size() < 22) {
            LOG.log(Level.FINE, "[Factors][" + symbol + "] insufficient history for reversal");
            return 0.0;
        }

        double p_now = closes.get(closes.size() - 1); // Most recent price
        double p_prev_month = closes.get(closes.size() - 22); // 1 month ago

        if (p_now <= 0 || p_prev_month <= 0) return 0.0;

        double val = (p_now / p_prev_month) - 1.0;
        LOG.log(Level.FINE, String.format(Locale.US, "[Factors][%s] Reversal(1m)=%.6f", symbol, val));
        return val;
    }

    @Override
    public double volatility(String symbol) {
        List<Double> closes = getAscendingCloses(symbol);
        if (closes.size() < 61) return 0.0;

        int end = closes.size();
        int start = Math.max(1, end - 60);
        List<Double> logReturns = new ArrayList<>();

        for (int i = start; i < end; i++) {
            double p0 = closes.get(i - 1);
            double p1 = closes.get(i);
            if (p0 <= 0 || p1 <= 0) continue;
            logReturns.add(Math.log(p1 / p0));
        }
        if (logReturns.size() < 2) return 0.0;

        double mean = logReturns.stream().mapToDouble(d -> d).average().orElse(0.0);
        double var = 0.0;
        for (double r : logReturns) var += Math.pow(r - mean, 2);

        double std = Math.sqrt(var / logReturns.size());
        double ann = std * Math.sqrt(252.0);

        LOG.log(Level.FINE, String.format(Locale.US, "[Factors][%s] Volatility=%.6f", symbol, ann));
        return ann;
    }

    @Override
    public double size(String symbol) {
        // Try cache first
        if (marketCapCache.containsKey(symbol)) {
            long mc = marketCapCache.get(symbol);
            return mc > 0 ? (double) mc : 0.0;
        }
        JSONObject ov = fetchOverview(symbol);
        if (ov == null) return 0.0;
        try {
            String s = ov.optString("MarketCapitalization", "");
            if (s == null || s.isEmpty()) return 0.0;
            long mc = Long.parseLong(s);
            marketCapCache.put(symbol, mc);
            return mc > 0 ? (double) mc : 0.0;
        } catch (Exception e) {
            LOG.log(Level.FINE, "[AlphaVantage][" + symbol + "] Failed to parse MarketCapitalization: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public double valueProxy(String symbol) {
        if (valueProxyCache.containsKey(symbol)) return valueProxyCache.get(symbol);
        JSONObject ov = fetchOverview(symbol);
        if (ov == null) return 0.0;
        // Prefer 1/PB if PB > 0, else 1/PE if PE > 0.
        double proxy = 0.0;
        try {
            double pb = parseDoubleSafe(ov.optString("PriceToBookRatio", ""));
            if (pb > 0) proxy = 1.0 / pb;
        } catch (Exception ignored) {}
        if (proxy == 0.0) {
            try {
                double pe = parseDoubleSafe(ov.optString("PERatio", ""));
                if (pe > 0) proxy = 1.0 / pe;
            } catch (Exception ignored) {}
        }
        valueProxyCache.put(symbol, proxy);
        return proxy;
    }

    // --- Helpers ---

    private List<Double> getAscendingCloses(String symbol) {
        if (!closeCache.containsKey(symbol)) {
            closeCache.put(symbol, fetchCloses(symbol));
        }
        return new ArrayList<>(closeCache.get(symbol).values());
    }

    private NavigableMap<LocalDate, Double> fetchCloses(String symbol) {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&datatype=json&symbol="
                + encode(symbol) + "&apikey=" + encode(apiKey);
        try {
            String json = httpGet(url);
            if (json == null || json.isEmpty()) return new TreeMap<>();
            return parseDailyCloses(json);
        } catch (IOException e) {
            LOG.warning("[AlphaVantage] Error: " + e.getMessage());
            return new TreeMap<>();
        }
    }

    private static String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // Longer timeouts for slow API
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (conn.getResponseCode() != 200) throw new IOException("HTTP " + conn.getResponseCode());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }

    private static NavigableMap<LocalDate, Double> parseDailyCloses(String json) {
        TreeMap<LocalDate, Double> map = new TreeMap<>();
        try {
            JSONObject root = new JSONObject(json);
            String tsKey = "Time Series (Daily)";
            if (!root.has(tsKey)) return map;

            JSONObject series = root.getJSONObject(tsKey);
            Iterator<String> it = series.keys();
            while (it.hasNext()) {
                String dateStr = it.next();
                try {
                    LocalDate d = LocalDate.parse(dateStr, DATE_FMT);
                    JSONObject day = series.getJSONObject(dateStr);
                    if (day.has("4. close")) {
                        map.put(d, day.getDouble("4. close"));
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
        }
        return map;
    }

    private static String encode(String s) {
        return s.replace(" ", "%20");
    }

    // ---- Fundamentals helpers ----
    private JSONObject fetchOverview(String symbol) {
        try {
            String url = "https://www.alphavantage.co/query?function=OVERVIEW&symbol="
                    + encode(symbol) + "&apikey=" + encode(apiKey);
            String json = httpGet(url);
            if (json == null || json.isEmpty()) return null;
            return new JSONObject(json);
        } catch (Exception e) {
            LOG.log(Level.FINE, "[AlphaVantage][" + symbol + "] OVERVIEW fetch error: " + e.getMessage());
            return null;
        }
    }

    private static double parseDoubleSafe(String s) {
        if (s == null) return 0.0;
        s = s.trim();
        if (s.isEmpty() || s.equalsIgnoreCase("None")) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}