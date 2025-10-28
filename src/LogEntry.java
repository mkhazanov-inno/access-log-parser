import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ipAddr, path, referer;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final UserAgent userAgent;
    private final int responseCode, responseSize;

    public LogEntry(String line) {

        int length = line.length();
        if (length > 1024) throw new LineTooLongException("Встречена строка, превышающая 1024 символа");

        //регексп, парсящий строку логов
        //211.71.205.41 - - [25/Sep/2022:06:25:20 +0300] "GET /november-reports/estimation/decisions/6373/65 HTTP/1.0" 200 8976 "-" "Mozilla/5.0 (compatible; MegaIndex.ru/2.0; +http://megaindex.com/crawler)"
        Pattern pattern = Pattern.compile("^(\\S+)\\s+" +     //ipAddr                1        211.71.205.41
                "(\\S+)\\s+" +                                      //-                     2        -
                "(\\S+)\\s+" +                                      //-                     3        -
                "\\[([^]]+)]\\s+" +                                 //time                  4        25/Sep/2022:06:25:20 +0300
                "\"(\\S+)\\s" +                                     //method                5        GET
                "(\\S+)\\s" +                                       //path                  6        /november-reports/estimation/decisions/6373/65
                "([^\"]+)\"\\s+" +                                  //protocol (skipped)    7        HTTP/1.0
                "(\\d{3})\\s+" +                                    //responseCode          8        200
                "(\\S+)\\s+" +                                      //responseSize          9        8976
                "\"([^\"]*)\"\\s+" +                                //referer               10       -
                "\"([^\"]*)\"");                                    //userAgent             11      Mozilla/5.0 (compatible; MegaIndex.ru/2.0; +http://megaindex.com/crawler)

        Matcher matcher = pattern.matcher(line);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        //если строка подходит, то наполняем
        if (matcher.find()) {

            this.ipAddr = matcher.group(1);
            this.time = ZonedDateTime.parse(matcher.group(4), formatter).toLocalDateTime();
            this.method = HttpMethod.valueOf(matcher.group(5));
            this.path = matcher.group(6);
            this.responseCode = Integer.parseInt(matcher.group(8));
            this.responseSize = Integer.parseInt(matcher.group(9));
            this.referer = matcher.group(10);
            this.userAgent = new UserAgent(matcher.group(11));

        } else {
            throw new IllegalArgumentException("Строка не соответствует паттерну: " + line);
        }

    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getPath() {
        return path;
    }

    public String getReferer() {
        return referer;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAddr='" + ipAddr + '\'' +
                ", path='" + path + '\'' +
                ", referer='" + referer + '\'' +
                ", time=" + time +
                ", method=" + method +
                ", userAgent=" + userAgent +
                ", responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                '}';
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        HEAD,
        OPTIONS,
        TRACE,
        CONNECT;

        private final String description;

        HttpMethod() {
            this.description = this.name();
        }

        HttpMethod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
