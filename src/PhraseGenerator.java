import java.io.*;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vijay on 2/5/15.
 *
 * TODO:
 *  - Sum of foreground bigram counts (fg_bigram=(counts),(distinct))
 *  - Number of distinct foreground bigrams
 *  - Sum of foreground unigram counts (fg_unigram=(counts),(distinct))
 *  - Number of distinct foreground unigrams
 *  - Number of background bigram counts (bg_bigram=(counts), (distinct))
 *  - Number of distinct background bigram counts
 */
public class PhraseGenerator {
    private static final int NUM_PHRASES = 20;
    private static PriorityQueue<PhraseScore> topPhrases = new PriorityQueue<PhraseScore>(NUM_PHRASES+1);
    private static final Pattern ATTR_PATTERN = Pattern.compile("Bx=(\\d+),Cx=(\\d+) Bxy=(\\d+),Cxy=(\\d+) By=(\\d+),Cy=(\\d+)");
    //private static final Pattern BIGRAM_COUNTS_PATTERN = Pattern.compile("BIGRAM_COUNTS (\\d+) (\\d+) (\\d+) (\\d+)");
    //private static final Pattern UNIGRAM_COUNTS_PATTERN = Pattern.compile("UNIGRAM_COUNTS (\\d+) (\\d+)");
    static long uniqueFgBigrams = 0;
    static long totalFgBigrams = 0;
    static long uniqueBgBigrams = 0;
    static long totalBgBigrams = 0;
    static long uniqueFgUnigrams = 0;
    static long totalFgUnigrams = 0;
    static int x = 0;

    /**
     * Stream through stdin, combine counts for each phrase, write to stdout
     * @throws IOException
     */
    private static void merge() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        String line;
        String prevPhrase = null;
        String attributes = "";

        // Counts used for smoothing and probability computation (stored at the top of the input file)
        parseBigramCounts(br.readLine());
        parseUnigramCounts(br.readLine());

        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String curPhrase = tokens[0];
            String curAttribute = tokens[1];

            if (curPhrase.equals(prevPhrase)) {
                attributes += " " + curAttribute;
            } else {
                // New phrase; compute score and add to max heap
                if (prevPhrase != null) {
                    PhraseScore p = computeScore(prevPhrase, attributes);
                    insertPhraseScore(p);
                }
                // Reset local variables
                prevPhrase = curPhrase;
                attributes = curAttribute;
            }
        }

        PhraseScore p;
        LinkedList<PhraseScore> output = new LinkedList<PhraseScore>();
        while ((p = topPhrases.poll()) != null) {
            output.addFirst(p);
        }

        for (PhraseScore s : output) {
            bw.write(s.toString());
        }

        br.close();
        bw.close();
    }

    private static void parseBigramCounts(String s) throws IOException {
        String[] tokens = s.split(" ");
        totalFgBigrams = Long.valueOf(tokens[1]);
        uniqueFgBigrams = Long.valueOf(tokens[2]);
        totalBgBigrams = Long.valueOf(tokens[3]);
        uniqueBgBigrams = Long.valueOf(tokens[4]);
    }

    private static void parseUnigramCounts(String s) throws IOException {
        String[] tokens = s.split(" ");
        totalFgUnigrams = Long.valueOf(tokens[1]);
        uniqueFgUnigrams = Long.valueOf(tokens[2]);
    }

    private static PhraseScore computeScore(String curPhrase, String attributes) {
        Matcher m = ATTR_PATTERN.matcher(attributes);
        if (!m.matches()) {
            return PhraseScore.invalidScore();
        }
        double bx,cx,bxy,cxy,by,cy;

        bx = Double.valueOf(m.group(1));
        cx = Double.valueOf(m.group(2));
        bxy = Double.valueOf(m.group(3));
        cxy = Double.valueOf(m.group(4));
        by = Double.valueOf(m.group(5));
        cy = Double.valueOf(m.group(6));

        double pLog = Math.log(cxy + 1) - Math.log(totalFgBigrams + uniqueFgBigrams);
        double qPhrasenessLog = Math.log(cx + 1)-Math.log(totalFgUnigrams + uniqueFgUnigrams) + Math.log(cy + 1) - Math.log(totalFgUnigrams + uniqueFgUnigrams);
        double qInformativenessLog = Math.log(bxy+1) - Math.log(totalBgBigrams + uniqueBgBigrams);

        double phraseness = Math.exp(pLog) * (pLog - qPhrasenessLog);
        double informativeness = Math.exp(pLog) * (pLog - qInformativenessLog);
;
        return new PhraseScore(curPhrase, phraseness, informativeness);
    }


    private static void insertPhraseScore(PhraseScore p) {
        topPhrases.add(p);
        if (topPhrases.size() > NUM_PHRASES) {
            topPhrases.poll();
        }
    }

    private static class PhraseScore implements Comparable {
        String phrase;
        double score;
        double phraseness;
        double informativeness;
        private static final PhraseScore invalid = new PhraseScore("", Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        PhraseScore(String phrase, double phraseness, double informativeness) {
            this.phrase = phrase;
            this.phraseness = phraseness;
            this.informativeness = informativeness;
            this.score = phraseness + informativeness;
        }

        static PhraseScore invalidScore() {
            return invalid;
        }

        @Override
        public String toString() {
            return (phrase + "\t" + score + "\t" + phraseness + "\t" + informativeness + "\n");
        }

        @Override
        public int compareTo(Object o) {
            return Double.compare(this.score, ((PhraseScore) o).score);
        }
    }

    public static void main(String[] args) throws Exception {
        merge();
    }
}
