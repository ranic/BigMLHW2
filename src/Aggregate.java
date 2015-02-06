import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vijay on 2/5/15.
 */
public class Aggregate {
    private static String[] STOP_WORD_ARRAY = {"a","about","above","across","after","afterwards","again","against","all","almost","alone","along","already","also","although","always","am","among","amongst","amoungst","amount","an","and","another","any","anyhow","anyone","anything","anyway","anywhere","are","around","as","at","back","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","between","beyond","bill","both","bottom","but","by","call","can","cannot","cant","co","computer","con","could","couldnt","cry","de","describe","detail","do","done","down","due","during","each","eg","eight","either","eleven","else","elsewhere","empty","enough","etc","even","ever","every","everyone","everything","everywhere","except","few","fifteen","fify","fill","find","fire","first","five","for","former","formerly","forty","found","four","from","front","full","further","get","give","go","had","has","hasnt","have","he","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","him","himself","his","how","however","hundred","i","ie","if","in","inc","indeed","interest","into","is","it","its","itself","keep","last","latter","latterly","least","less","ltd","made","many","may","me","meanwhile","might","mill","mine","more","moreover","most","mostly","move","much","must","my","myself","name","namely","neither","never","nevertheless","next","nine","no","nobody","none","noone","nor","not","nothing","now","nowhere","of","off","often","on","once","one","only","onto","or","other","others","otherwise","our","ours","ourselves","out","over","own","part","per","perhaps","please","put","rather","re","same","see","seem","seemed","seeming","seems","serious","several","she","should","show","side","since","sincere","six","sixty","so","some","somehow","someone","something","sometime","sometimes","somewhere","still","such","system","take","ten","than","that","the","their","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","thereupon","these","they","thick","thin","third","this","those","though","three","through","throughout","thru","thus","to","together","too","top","toward","towards","twelve","twenty","two","un","under","until","up","upon","us","very","via","was","we","well","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","with","within","without","would","yet","you","your","yours","yourself","yourselves"};
    private static final Set<String> STOP_WORDS = new HashSet<String>(Arrays.asList(STOP_WORD_ARRAY));
    private static final String FOREGROUND_DECADE = "1960";

    /**
     * Reads foreground and background counts for phrases from stdin
     * Writes merged counts to stdout in the format specified by outFormat
     *
     * @param outFormat
     *      Format of aggregated counts
     * @throws IOException
     */
    private static void aggregate(String outFormat) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        String line;
        String prevPhrase = null, curPhrase = null;
        int foreCount = 0;
        int backCount = 0;

        while ((line = br.readLine()) != null) {
            // Parse the line (of the form <text>\t<decade>\t<count>)
            String[] tokens = line.split("\t");
            curPhrase = tokens[0];
            String decade = tokens[1];
            int count = Integer.valueOf(tokens[2]);

            // Ignore all uninformative phrases
            if (containsStopWords(curPhrase)) {
                continue;
            }

            // New phrase; flush the counts of the previous phrase
            if (!curPhrase.equals(prevPhrase)) {
                if (prevPhrase == null) {
                    prevPhrase = curPhrase;
                } else {
                    bw.write(String.format(outFormat, prevPhrase, backCount, foreCount));

                    // Reset local variables
                    prevPhrase = curPhrase;
                    foreCount = 0;
                    backCount = 0;
                }
            }

            // Increment either foreground or background counter for this phrase
            if (decade.equals(FOREGROUND_DECADE)) {
                foreCount += count;
            } else {
                backCount += count;
            }
        }

        bw.write(String.format(outFormat, curPhrase, backCount, foreCount));
        br.close();
        bw.close();
    }

    private static boolean containsStopWords(String phrase) {
        String[] tokens = phrase.split(" ");
        for (String token : tokens) {
            if (STOP_WORDS.contains(token))
                return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || Math.abs(Integer.valueOf(args[0])) > 1) {
            System.out.println("Must supply argument of either 1 (bigram) or 0 (unigram)");
            System.out.println("Usage Example: cat unigram.txt | sort -k1 | java -Xmx128m Aggregate 0 > unigram_processed.txt");
            return;
        }
        int isBigram = Integer.valueOf(args[0]);

        // Set format of output based on whether input is unigram or bigram
        String outFormat = (isBigram == 0) ? "%s\tBx=%d,Cx=%d\n" : "%s\tBxy=%d,Cxy=%d\n";
        aggregate(outFormat);
    }

}
