import java.io.*;

/**
 * Created by vijay on 2/5/15.
 */
public class PhraseGenerator {

    // Data is of form
        // key1, value1
        // key1, value2
        // key1, value3
    // Merge these three

    private static void merge() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        String line;
        String prevPhrase = null;
        String value = "";
        int x = 0;

        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String curPhrase = tokens[0];
            String curValue = tokens[1];

            // New phrase; flush the counts of the previous phrase
            if (curPhrase.equals(prevPhrase)) {
                value += " " + curValue;
            } else {
                if (prevPhrase != null) {
                    bw.write(String.format("%s\t%s\n", prevPhrase, value));
                }
                // Reset local variables
                prevPhrase = curPhrase;
                value = curValue;
            }
        }

        br.close();
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        merge();
    }
}
