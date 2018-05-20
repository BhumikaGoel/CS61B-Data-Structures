import java.util.Scanner;
import java.util.regex.MatchResult;

public class P3 {

    public static void main(String... ignored) {

        String pattern = "([0-9]+)\\s+";
        Scanner scanner = new Scanner(System.in);
        int matcher, start, end;
        while (/*some condition */ != null) {
            MatchResult match = scanner.match();
            matcher = Integer.parseInt(match.group(1));
            start = 0;
            end = 0;
            for (int i = 0; i < matcher; i++) {
                start += Math.pow(10, i);
                end   += 3 * Math.pow(10, i);
            }

            for (int k = start+1; k < end; k=k+2) {
                if (/*some condition */) {
                    System.out.printf("The smallest good numeral of length %d "
                            + "is %d\n\n", matcher, k);
                }
            }
        }
    }


}
