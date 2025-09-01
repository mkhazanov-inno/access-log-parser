import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String path;
        boolean fileExists;
        boolean isDirectory;
        int counter = 0;


        while (true) {
            System.out.println("Введите путь к файлу:");
            path = sc.nextLine();
            File file = new File(path);
            fileExists = file.exists();
            isDirectory = file.isDirectory();
            if (!fileExists){
                System.out.println("Введенная строка не является путем к файлу: объект не существует");
                continue;
            } else if (isDirectory) {
                System.out.println("Введенная строка не является путем к файлу: это каталог");
                continue;
            }
            counter++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + counter);


        }

    }
}
