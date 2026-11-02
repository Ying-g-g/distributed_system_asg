import java.util.Scanner;

public class UserInput {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String md5HashValue = "";
        int noOfThreads = 0;
        int passwordLength = 0;

        while(true) {

                System.out.print("Please enter the MD5 hash value (no symbols or spaces): ");
                md5HashValue = scanner.nextLine().trim();
                if (md5HashValue.matches("[a-fA-F0-9]{32}")) {
                    break;
                } else {
                    System.out.println("INVALID INPUT. The MD5 hash value must be a 32-character hexadecimal string without symbols and spaces.");
                }
        }

        while(true) {

            try {
                System.out.print("Please enter the number of threads (1-10): ");
                noOfThreads = Integer.parseInt(scanner.nextLine());
                if (noOfThreads >= 1 && noOfThreads <= 10) {
                    break;
                } else {
                    System.out.println("INVALID INPUT. The number of threads must be within 1-10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("INVALID INPUT. The number of threads must be a number.");
            }
        }

        while(true) {

            try {
                System.out.print("Please enter the password length (2-6): ");
                passwordLength = Integer.parseInt(scanner.nextLine());
                if (passwordLength >= 2 && passwordLength <= 6) {
                    break;
                } else {
                    System.out.println("INVALID INPUT. The password length must be within 2-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("INVALID INPUT. The password length must be a number.");
            }
        }

        scanner.close();

        MD5Crack.main(new String[] 
            {
                md5HashValue,
                String.valueOf(noOfThreads),
                String.valueOf(passwordLength)
            }
        );
    }
}