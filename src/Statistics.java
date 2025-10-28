import java.time.Duration;
import java.time.LocalDateTime;


public class Statistics {
    private double totalTraffic;
    private LocalDateTime minTime, maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {
        this.totalTraffic += entry.getResponseSize();
        if (this.minTime.isAfter(entry.getTime())) this.minTime = entry.getTime();
        if (this.maxTime.isBefore(entry.getTime())) this.maxTime = entry.getTime();
    }

    public double getTrafficRate() {
        double diff = Duration.between(minTime, maxTime).toSeconds() / 3600.0;
        return totalTraffic / diff;
    }

    public int getTrafficRateBeautified() {
        double tr = getTrafficRate();
        int gb = (int) (tr / 1073741824);
        int mb = (int) (tr - gb * 1073741824) / 1048576;
        int kb = (int) (tr - gb * 1073741824 - mb * 1048576) / 1024;
        int b = (int) (tr - gb * 1073741824 - mb * 1048576 - kb * 1024);


    }

    public double getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", traffic/hour=" + getTrafficRate() +
                '}';
    }
}
