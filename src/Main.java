import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

            int maxLength = Integer.MIN_VALUE;
            int minLength = Integer.MAX_VALUE;
            int totalLines = 0;

            //построчное чтение
            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) throw new LineTooLongException("Встречена строка, превышающая 1024 символа");
                    if (length < minLength) minLength = length;
                    if (length > maxLength) maxLength = length;
                    totalLines++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }

            System.out.println("Общее количество строк: " + totalLines);
            System.out.println("Самая длинная строка: " + maxLength + " символов");
            System.out.println("Самая короткая строка: " + minLength + " символов");
            break;
        }

    }
}
