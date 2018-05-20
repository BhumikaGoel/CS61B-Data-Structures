import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(0) to TO.charAt(0), etc., leaving other characters
     *  unchanged.  FROM and TO must have the same length. */
    private Reader src;
    private String from;
    private String to;
    public TrReader(Reader str, String from, String to) {
        // FILL IN
        this.src = str;
        this.from = from;
        this.to = to;
    }

    public void close() throws IOException {
        this.src.close();
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        int convertRead = src.read(cbuf, off, len);
        for (int i = off; i < off + convertRead; i ++) {
            cbuf[i] = converter(cbuf[i]);
        }
        return convertRead;
    }

    private char converter(char in) throws IOException {
        int i = from.indexOf(in);
        if (i != -1) {
            return to.charAt(i);
        } else {
            return in;
        }
    }

    // FILL IN
    // NOTE: Until you fill in the right methods, the compiler will
    //       reject this file, saying that you must declare TrReader
    //     abstract.  Don't do that; define the right methods instead!
}


