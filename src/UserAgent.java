import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgent {
    private final String browser, OS;
    //userAgent             11      Mozilla/5.0 (compatible; MegaIndex.ru/2.0; +http://megaindex.com/crawler)

    public UserAgent(String line) {

        this.browser = line.split(" ")[0];

        //первые скобки
        Matcher firstBracketsMatch = Pattern.compile("\\(([^)]+)\\)").matcher(line);

        //если в запросе первые скобки и контент в них, парсим контент
        if (firstBracketsMatch.find()) {
            String firstBrackets = firstBracketsMatch.group(1);
            String[] parts = firstBrackets.split(";");
            if (parts.length >= 2) {
                String fragment = parts[1].trim(); //очищаем от пробелов
                this.OS = fragment.split("/")[0]; //берем часть до слэша
            } else {
                this.OS = "";
            }

        } else {
            this.OS = "";
        }
    }

    public String getBrowser() {
        return browser;
    }

    public String getOS() {
        return OS;
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "browser='" + browser + '\'' +
                ", OS='" + OS + '\'' +
                '}';
    }
}
