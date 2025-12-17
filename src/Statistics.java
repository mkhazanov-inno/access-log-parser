import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;


public class Statistics {
    private long totalTraffic, realUserVisitCount, errorCount;
    private LocalDateTime minTime, maxTime;
    private HashSet<String> pages200 = new HashSet<>();
    private HashSet<String> pages404 = new HashSet<>();
    private HashSet<String> realUserIPs = new HashSet<>();
    private HashSet<String> referers = new HashSet<>();
    private HashMap<String, Integer> OSFrequency = new HashMap<>();
    private HashMap<String, Integer> browserFrequency = new HashMap<>();
    private HashMap<LocalDateTime, Integer> visitsPerSecond = new HashMap<>();
    private HashMap<String, Integer> visitsPerIP = new HashMap<>();


    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.realUserVisitCount = 0;
    }

    public void addEntry(LogEntry entry) {
        this.totalTraffic += entry.getResponseSize();

        if (this.minTime.isAfter(entry.getTime())) this.minTime = entry.getTime();
        if (this.maxTime.isBefore(entry.getTime())) this.maxTime = entry.getTime();

        if (entry.getResponseCode() == 200) this.pages200.add(entry.getPath());
        if (entry.getResponseCode() == 404) this.pages404.add(entry.getPath());
        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600) errorCount++;

        OSFrequency.merge(entry.getUserAgent().getOS(), 1,  (a, b) -> a + b);
        browserFrequency.merge(entry.getUserAgent().getBrowser(), 1, (a, b) -> a + b);
        referers.add(entry.getReferer().transform(ref -> URI.create(ref).getHost()));

      /*  if (this.OSFrequency.containsKey(entry.getUserAgent().getOS()) && !Objects.equals(entry.getUserAgent().getOS(), "")) {
            this.OSFrequency.replace(entry.getUserAgent().getOS(), OSFrequency.get(entry.getUserAgent().getOS()) + 1);
        } else if (!Objects.equals(entry.getUserAgent().getOS(), "")) {
            this.OSFrequency.put(entry.getUserAgent().getOS(), 1);
        }

        if (this.browserFrequency.containsKey(entry.getUserAgent().getBrowser()) && !Objects.equals(entry.getUserAgent().getBrowser(), "")) {
            this.browserFrequency.replace(entry.getUserAgent().getBrowser(), browserFrequency.get(entry.getUserAgent().getBrowser()) + 1);
        } else if (!Objects.equals(entry.getUserAgent().getBrowser(), "")) {
            this.browserFrequency.put(entry.getUserAgent().getBrowser(), 1);
        } */


        if (!entry.getUserAgent().isBot()) {
            realUserVisitCount++;
            this.realUserIPs.add(entry.getIpAddr());
            visitsPerSecond.merge(entry.getTime(), 1, (a, b) -> a + b);
            visitsPerIP.merge(entry.getIpAddr(), 1, (a, b) -> a + b);
            //long a = entry.getTime().toEpochSecond(ZoneOffset.ofHours(0));
            //LocalDateTime b = LocalDateTime.ofEpochSecond(a, 0, ZoneOffset.ofHours(0));

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

    public HashMap<String, Double> getBrowserFrequency() {
        int totalCount = 0;
        for (Map.Entry<String, Integer> entry : browserFrequency.entrySet()) {
            totalCount += entry.getValue();
        }
        HashMap<String, Double> out = new HashMap<>();

        for (Map.Entry<String, Integer> entry : browserFrequency.entrySet()) {
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

    public double getLogDuration() {
        if (minTime.equals(maxTime)) throw new IllegalArgumentException("Cannot compute duration because log start and end times are the same");
        return Duration.between(minTime, maxTime).toSeconds() / 3600.0;
    }

    public double getTrafficRate() {
        return totalTraffic / getLogDuration();
    }

    public String getTrafficRateBeautified() {
        double tr = getTrafficRate();
        int gb = (int) (tr / 1073741824);
        int mb = (int) (tr - gb * 1073741824) / 1048576;
        int kb = (int) (tr - gb * 1073741824 - mb * 1048576) / 1024;
        int b = (int) (tr - gb * 1073741824 - mb * 1048576 - kb * 1024);
        return gb + " GB, " + mb + " MB, " + kb + " KB, " + b + " B";

    }

    public double getVisitsRate() {
        return realUserVisitCount / getLogDuration();
    }

    public double getAverageErrors() {
        return errorCount / getLogDuration();
    }

    public double getAverageVisitsPerUser() {
        if (realUserIPs.isEmpty()) return 0;
        return (double) realUserVisitCount / realUserIPs.size();
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream().max(Integer::compareTo).orElse(0);
    }

    public HashSet<String> getReferers() {
        return referers;
    }

    public int getPeakVisitsPerIP() {
        return visitsPerIP.values().stream().max(Integer::compareTo).orElse(0);
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public HashSet<String> getPages200() {

        return pages200;
    }

    public HashSet<String> getPages404() {

        return pages404;
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
