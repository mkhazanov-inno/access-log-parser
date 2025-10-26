import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String path;
        //int counter = 0;


        while (true) {

            //получение пути
            System.out.println("Введите путь к файлу:");
            path = sc.nextLine();
            File file = new File(path);

            //проверка наличия файла
            if (!file.exists()) {
                System.out.println("Введенная строка не является путем к файлу: объект не существует");
                continue;
            } else if (file.isDirectory()) {
                System.out.println("Введенная строка не является путем к файлу: это каталог");
                continue;
            }

            int maxLength = Integer.MIN_VALUE;
            int minLength = Integer.MAX_VALUE;
            int totalLines = 0;


            //инициализируем карту с логами
            HashMap<Integer, HashMap<String, String>> logMap = new HashMap<>();

            //построчное чтение
            try {

                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;

                while ((line = reader.readLine()) != null) {

                    HashMap<String, String> parsed = getParsed(line);
                    logMap.put(totalLines, parsed);
                    totalLines++;
                }

            } catch (Exception ex) {

                ex.printStackTrace();
                System.exit(0);
            }

            //заводим счетчики для яндекса и гугла
            int gCount = 0;
            int yCount = 0;

            //построчно парсим юзерагентов
            for (int i = 0; i < logMap.size(); i++) {
                String entry = logMap.get(i).get("userAgent");

                /*if (entry.contains("Googlebot")) gCount++;
                if (entry.contains("YandexBot")) yCount++;*/

                //выделяем часть в первых скобках
                Matcher firstBracketsMatch = Pattern.compile("\\(([^)]+)\\)").matcher(entry);

                //если в запросе первые скобки и контент в них, парсим контент
                if (firstBracketsMatch.find()) {
                    String firstBrackets = firstBracketsMatch.group(1);
                    String[] parts = firstBrackets.split(";\\s*");
                    if (parts.length >= 2) {
                        String fragment = parts[1];
                        fragment = fragment.split("/")[0]; //берем часть до слэша
                        if (fragment.equals("Googlebot")) gCount++;
                        if (fragment.equals("YandexBot")) yCount++;
                    }
                }


            }

            //выводим счетчики
            System.out.println("Общее количество запросов: " + totalLines + 1);
            System.out.println("- - -");
            System.out.println("Запросы от YandexBot: " + yCount);
            System.out.println("Запросы от Googlebot: " + gCount);
            System.out.println("- - -");
            double yPercent = (double) yCount / totalLines * 100;
            System.out.println("Доля запросов от YandexBot: " + yPercent + " %");
            double gPercent = (double) gCount / totalLines * 100;
            System.out.println("Доля запросов от Googlebot: " + gPercent + " %");

            break;
        }

    }

    private static HashMap<String, String> getParsed(String line) {

        int length = line.length();
        if (length > 1024) throw new LineTooLongException("Встречена строка, превышающая 1024 символа");

        //регексп, парсящий строку логов
        //211.71.205.41 - - [25/Sep/2022:06:25:20 +0300] "GET /november-reports/estimation/decisions/6373/65 HTTP/1.0" 200 8976 "-" "Mozilla/5.0 (compatible; MegaIndex.ru/2.0; +http://megaindex.com/crawler)"
        Pattern pattern = Pattern.compile("^(\\S+)\\s+" +     //ip          211.71.205.41
                "(\\S+)\\s+" +                                      //-            -
                "(\\S+)\\s+" +                                      //-            -
                "\\[([^]]+)]\\s+" +                                 //dateTime     25/Sep/2022:06:25:20 +0300
                "\"(\\S+)\\s" +                                     //method       GET
                "(\\S+)\\s" +                                       //request      /november-reports/estimation/decisions/6373/65
                "([^\"]+)\"\\s+" +                                  //protocol     HTTP/1.0
                "(\\d{3})\\s+" +                                    //code         200
                "(\\S+)\\s+" +                                      //bytes        8976
                "\"([^\"]*)\"\\s+" +                                //referer      -
                "\"([^\"]*)\"");                                    //userAgent    Mozilla/5.0 (compatible; MegaIndex.ru/2.0; +http://megaindex.com/crawler)

        Matcher matcher = pattern.matcher(line);


        HashMap<String, String> parsed = null;

        //если строка подходит, то наполняем хэшмапу строки
        if (matcher.find()) {
            parsed = new HashMap<>();

            parsed.put("IP", matcher.group(1));
            parsed.put("dateTime", matcher.group(4));
            parsed.put("method", matcher.group(5));
            parsed.put("request", matcher.group(6));
            parsed.put("protocol", matcher.group(7));
            parsed.put("statusCode", matcher.group(8));
            parsed.put("bytes", matcher.group(9));
            parsed.put("referer", matcher.group(10));
            parsed.put("userAgent", matcher.group(11));

        }
        return parsed;
    }
}
