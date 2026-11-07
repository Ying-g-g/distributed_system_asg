import java.sql.Timestamp;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

public class MD5Crack {
    //Declare all variables
    static volatile boolean found = false;
    static volatile String foundPassword = null;
    static volatile int foundThreadId = -1;
    static AtomicLong attempts = new AtomicLong(0);  //atomic counter for the number of attempts made   

    //Function to call out all executing functions
    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Starting MD5 crack at " + timestamp);
        String targetHash = args[0];  //get the target hash from the command line arguments
        int numThreads = Integer.parseInt(args[1]);  //get the number of threads
        int pwLength = Integer.parseInt(args[2]);  //get the password length
        attempts.set(0);  //reset the atomic counter for the number of attempts made
        long searchSpace = 1;
        for (int i = 0; i < pwLength; i++) searchSpace *= 95;  //calculate the total search space based on the password length
        final long finalSearchSpace = searchSpace;  //store the search space in a final variable for use in the lambda expression
        Thread[] threads = createThreads(numThreads, searchSpace, pwLength, targetHash);  //create the threads

        //Track proggress of the search
        Thread progressThread = new Thread(() -> {
            while (!found) {
                try {
                    Thread.sleep(5000);  //sleep for 5 seconds
                } catch (InterruptedException e) {
                    return;  //if interrupted, exit the progress tracking thread
                }
                long currentAttempts = attempts.get();  //get the current number of attempts made
                double progress = (double) currentAttempts / finalSearchSpace * 100;  //calculate the progress percentage
                System.out.printf("Progress: %.2f%%, Attempts: %d%n", progress, currentAttempts);  //print out the progress and number of attempts
            }
        });
        progressThread.setDaemon(true);  //set the progress thread as a daemon thread so it doesn't prevent the program from exiting
        progressThread.start();  //start the progress tracking thread
        
        for (Thread thread : threads) {
            thread.start();  //start each thread
        }
        for (Thread thread : threads) {
            try {
                thread.join();  //wait for each thread to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        progressThread.interrupt();  //interrupt the progress tracking thread once all threads have finished
        try {
            progressThread.join();  //wait for the progress tracking thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
        if (found) {
            //System.out.println("Password found: " + foundPassword + " by thread " + foundThreadId);
            System.out.println("Password found: " + foundPassword);
            System.out.println("Thread ID: " + foundThreadId);
        } else {
            System.out.println("Password not found.");
        }

        System.out.println("Ending MD5 crack at " + endTimestamp);
        System.out.println("Execution time: " + (endTimestamp.getTime() - timestamp.getTime()) + " milliseconds");
        
    }

    static Thread[] createThreads(int numThreads, long searchSpace, int pwLength, String targetHash) {
        long chunkSize = searchSpace / numThreads;
        long remainder = searchSpace % numThreads;  //in case the search space is not evenly divisible by the number of threads

        Thread[] threads = new Thread[numThreads];  //array to hold threads once created
        long start = 0;                             //tracker of where the current thread should start searching
        for (int t = 0; t < numThreads; t++) {
            long size = chunkSize + (t < remainder ? 1 : 0);  //distribute the remainder among the first few threads
            /* will assign the remainders for the front threads first
               for example if remainder  = 1 will assign extra one to thread 0, remainder = 2 will assign one for thread 0 and one for thread 1*/
            long rangeStart = start;
            long rangeEnd = start + size - 1;  //calculate the end of the range for the current thread
            int threadId = t + 1;  //store the thread id for use in the lambda expression
            threads[t] = new Thread(() -> {
                searchRange(rangeStart, rangeEnd, pwLength, targetHash, threadId);  //call the searchRange function for the current thread
            });
            start = rangeEnd + 1;  //update the start position for the next thread
        }
        return threads;
    }

    static void searchRange(long start, long end, int pwLength, String targetHash, int threadId) {
        for (long i = start; i <= end && !found; i++) {
            if (found) return;
            char[] guess = new char[pwLength];
            long temp = i;
            for (int j = pwLength - 1; j >= 0; j--) {
                guess[j] = (char) (32 + (temp % 95));  //generate the password guess based on the current number
                temp /= 95;  //divide by 95 to move to the next character in the guess
            }
            String guessStr = new String(guess);  //convert the character array to a string
            String guessHash = md5(guessStr);  //calculate the MD5 hash of

            attempts.incrementAndGet();  //increment the atomic counter for the number of attempts made 

            if (guessHash.equalsIgnoreCase(targetHash)) {  //check if the guess hash matches the target hash
                found = true;  //set the found flag to true
                foundPassword = guessStr;  //store the found password
                foundThreadId = threadId;  //store the thread id that found the password
                //System.out.println("Thread " + threadId + " found the password: " + guessStr);  //print out which thread found the password
                return;
            }
        }
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  //get an instance of the MD5 message digest
            byte[] messageDigest = md.digest(input.getBytes());  //calculate the MD5 hash of the input string
            BigInteger no = new BigInteger(1, messageDigest);  //convert the byte array to a BigInteger
            String hashtext = no.toString(16);  //convert the BigInteger to a hexadecimal string
            while (hashtext.length() < 32) {  //pad with leading zeros if necessary
                hashtext = "0" + hashtext;
            }
            return hashtext;  //return the resulting MD5 hash
        } catch (NoSuchAlgorithmException e) {  //catch any exceptions related to the MD5 algorithm not being available
            throw new RuntimeException(e);
        }
    }
}