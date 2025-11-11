import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;


public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime, maxTime;
    private HashSet<String> pages = new HashSet<>();
    private HashMap<String, Integer> OSFrequency = new HashMap<>();

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {

        this.totalTraffic += entry.getResponseSize();

        if (this.minTime.isAfter(entry.getTime())) this.minTime = entry.getTime();
        if (this.maxTime.isBefore(entry.getTime())) this.maxTime = entry.getTime();

        if (entry.getResponseCode() == 200) this.pages.add(entry.getPath());

        if (this.OSFrequency.containsKey(entry.getUserAgent().getOS()) && !Objects.equals(entry.getUserAgent().getOS(), "")) {
            this.OSFrequency.replace(entry.getUserAgent().getOS(), OSFrequency.get(entry.getUserAgent().getOS()), OSFrequency.get(entry.getUserAgent().getOS()) + 1);
        } else if (!Objects.equals(entry.getUserAgent().getOS(), "")) {
            this.OSFrequency.put(entry.getUserAgent().getOS(), 1);
        }
    }

    public HashMap<String, Double> getOSFrequency() {
        int totalCount = 0;
        for (Map.Entry<String, Integer> entry : OSFrequency.entrySet()) {
            totalCount += entry.getValue();
        }
        HashMap<String, Double> out = new HashMap<>();

        for (Map.Entry<String, Integer> entry : OSFrequency.entrySet()) {
            String k = entry.getKey();
            double v = (double) entry.getValue() / totalCount;
            if (v > 0.02) {
                out.put(k, v);
            } else if (out.containsKey("Other")){
                double tempv = out.get("Other");
                out.replace("Other", tempv + v);
            } else {
                out.put("Other", v);
            }

        }

        return out;

    }

    public HashMap<String, Double> getOSFrequencyDetailed() {
        int totalCount = 0;
        for (Map.Entry<String, Integer> entry : OSFrequency.entrySet()) {
            totalCount += entry.getValue();
        }
        HashMap<String, Double> out = new HashMap<>();

        for (Map.Entry<String, Integer> entry : OSFrequency.entrySet()) {
            String k = entry.getKey();
            double v = (double) entry.getValue() / totalCount;
            out.put(k, v);


        }

        return out;

    }

    public double getTrafficRate() {
        double diff = Duration.between(minTime, maxTime).toSeconds() / 3600.0;
        return totalTraffic / diff;
    }

    public String getTrafficRateBeautified() {
        double tr = getTrafficRate();
        int gb = (int) (tr / 1073741824);
        int mb = (int) (tr - gb * 1073741824) / 1048576;
        int kb = (int) (tr - gb * 1073741824 - mb * 1048576) / 1024;
        int b = (int) (tr - gb * 1073741824 - mb * 1048576 - kb * 1024);
        return gb + " GB, " + mb + " MB, " + kb + " KB, " + b + " B";

    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public HashSet<String> getPages() {

        return pages;
    }


    @Override
    public String toString() {
        return "Statistics{" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", traffic/hour=" + getTrafficRateBeautified() +
                '}';
    }
}
