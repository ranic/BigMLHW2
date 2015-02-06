import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Lines of the form <word, phrase> are requests from phrase for word's counts
 *
 * This program takes in the sorted together requests and counts.
 * It outputs the counts
 *
 * Created by vijay on 2/5/15.
 */
public class MessageUnigramCombiner {


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        String line;
        String curWord = null;
        int foreCount = 0;
        int backCount = 0;

        Pattern responsePattern = Pattern.compile("([a-z]+)\\tBx=(\\d+),Cx=(\\d+)");
        Pattern requestPattern = Pattern.compile("([a-z]+),([a-z]+) ([a-z]+)");
        Matcher responseMatcher, requestMatcher;

        // For each word in each phrase, output <word>,<phrase> pairs
        while ((line = br.readLine()) != null) {
            responseMatcher = responsePattern.matcher(line);
            requestMatcher = requestPattern.matcher(line);

            // This line contains an attribute value pair
            if (responseMatcher.matches()) {
                curWord = responseMatcher.group(1);
                backCount = Integer.valueOf(responseMatcher.group(2));
                foreCount = Integer.valueOf(responseMatcher.group(3));
            } else if (requestMatcher.matches() && curWord != null) {
                // This line contains a request
                String requestWord = requestMatcher.group(1);
                String firstWord = requestMatcher.group(2);
                String secondWord = requestMatcher.group(3);
                String bigram = firstWord + " " + secondWord;
                if (requestWord.equals(curWord)) {
                    // Check whether first word or second word of bigram matches count
                    String format = (firstWord.equals(curWord)) ? "%s\tBx=%d,Cx=%d\n" : "%s\tBy=%d,Cy=%d\n";
                    // Respond to request with foreCount and backCount
                    bw.write(String.format(format, bigram, backCount, foreCount));
                } else {
                    // No information for this word exists (this should never happen)
                    String format = (firstWord.equals(curWord)) ? "%s\tBx=0,Cx=0\n" : "%s\tBy=0,Cy=0\n";
                    bw.write(String.format(format, bigram));
                }
            }
        }

        br.close();
        bw.close();
    }
}
