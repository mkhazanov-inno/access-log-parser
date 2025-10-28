import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

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

            int totalLines = 0;


            //инициализируем карту с логами
            HashMap<Integer, LogEntry> logMap = new HashMap<>();
            Statistics st = new Statistics();
            //построчное чтение
            try {

                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;

                while ((line = reader.readLine()) != null) {

                    LogEntry parsed = new LogEntry(line);
                    logMap.put(totalLines, parsed);
                    st.addEntry(parsed);
                    totalLines++;
                    if (totalLines > 50) {
                        System.out.println(st);
                        break;
                    }

                }

            } catch (Exception ex) {

                ex.printStackTrace();
                System.exit(0);
            }


//            //заводим счетчики для яндекса и гугла
//            int gCount = 0;
//            int yCount = 0;
//
//            //построчно парсим юзерагентов
//            for (int i = 0; i < 50; i++) {
//                String entry = logMap.get(i).get("userAgent");
//
//                /* вариант, считающий все строки с googlebot и yandexbot, не только в первых скобках
//                if (entry.contains("Googlebot")) gCount++;
//                if (entry.contains("YandexBot")) yCount++;*/
//
//                //выделяем часть в первых скобках
//                Matcher firstBracketsMatch = Pattern.compile("\\(([^)]+)\\)").matcher(entry);
//
//                //если в запросе первые скобки и контент в них, парсим контент
//                if (firstBracketsMatch.find()) {
//                    String firstBrackets = firstBracketsMatch.group(1);
//                    String[] parts = firstBrackets.split(";");
//                    if (parts.length >= 2) {
//                        String fragment = parts[1].trim(); //очищаем от пробелов
//                        fragment = fragment.split("/")[0]; //берем часть до слэша
//                        if (fragment.equals("Googlebot")) gCount++;
//                        if (fragment.equals("YandexBot")) yCount++;
//                    }
//                }
//
//
//            }
//
//            //выводим счетчики
//            System.out.println("Общее количество запросов: " + totalLines);
//            System.out.println("- - -");
//            System.out.println("Запросы от YandexBot: " + yCount);
//            System.out.println("Запросы от Googlebot: " + gCount);
//            System.out.println("- - -");
//            double yPercent = (double) yCount / totalLines * 100;
//            System.out.println("Доля запросов от YandexBot: " + yPercent + " %");
//            double gPercent = (double) gCount / totalLines * 100;
//            System.out.println("Доля запросов от Googlebot: " + gPercent + " %");
            System.out.println(st);
            break;
        }

    }


}
