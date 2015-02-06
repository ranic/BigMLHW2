import java.io.*;

/**
 * Created by vijay on 2/5/15.
 */
public class MessageGenerator {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        String line;

        // For each word in each phrase, output <word>,<phrase> pairs
        while ((line = br.readLine()) != null) {
            String phrase = line.split("\t")[0];
            String[] words = phrase.split(" ");
            for (String word : words) {
                bw.write(String.format("%s,%s\n", word, phrase));
            }
        }

        br.close();
        bw.close();
    }
}
