import java.util.Scanner;

public class UserInput {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the MD5 hash value: ");
        String md5Hash = scanner.nextLine();

        System.out.println("Please enter the number of threads: ");
        int threads = Integer.parseInt(scanner.nextLine());

        System.out.println("Please enter the password length: ");
        int passwordLength = Integer.parseInt(scanner.nextLine());
    }
}