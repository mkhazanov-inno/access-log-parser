import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите первое число:");
        int a = sc.nextInt();
        System.out.println("Введите второе число");
        int b = sc.nextInt();


        System.out.println("Сумма: " + (a + b));
        System.out.println("Разница: " + (a - b));
        System.out.println("Произведение: " + (a * b));
        System.out.println("Частное: " + ((double)a / b));


    }
}
