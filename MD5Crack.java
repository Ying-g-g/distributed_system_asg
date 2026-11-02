import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class MD5Crack {

    public static void main(String[] args) {

        Timestamp timesStart = new Timestamp(System.currentTimeMillis());
        Boolean isFound = false;
        
        //Retrieve user input
        String targetHash = args[0];
        int numThreads = Integer.parseInt(args[1]);
        int pwLength = Integer.parseInt(args[2]);

        //Declare search space
        long searchSpace = (long) Math.pow(95, pwLength);

        String guessStr = "";

        int nextMilestone = 25;

        for (long n = 0; n < searchSpace; n++) {
            char[] guess = new char[pwLength];
            long temp = n;

            for (int i = pwLength - 1; i >= 0; i--) {
                guess[i] = (char) (32 + (temp % 95));
                temp /= 95;
            }

            String guessString = new String(guess);
            String hash = getMd5(guessString);

            if(hash.equalsIgnoreCase(targetHash)) {
                System.out.println("Password found: " + guessString);
                isFound = true;
                break;
            }

            double percentComplete = ((double) n / searchSpace) * 100;
            if (percentComplete >= nextMilestone) {
                System.out.println("Progress: " + nextMilestone + "%");
                nextMilestone += 25;
            }
        }

        if(!isFound) {
            System.out.println("Password not found.");
        }
        
        Timestamp timesEnd = new Timestamp(System.currentTimeMillis());

        System.out.println(timesStart);
        System.out.println(timesEnd);
        System.out.println("Time taken: " + (timesEnd.getTime() - timesStart.getTime()) + " milliseconds");
    }

    public static String getMd5(String input) {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() returns array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}