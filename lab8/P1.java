import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class P1 {

    public static void main(String... ignored) {
        Scanner scanner = new Scanner(System.in);
        List <Image> images = new ArrayList <Image>();
        Image image = new Image();
        String store;
        while ( scanner.hasNextLine() ) {
            store = scanner.nextLine();
            if (store.equals("")) {
                images.add(image);
                image = new Image();
                continue;
            }

            int i = 1;
            for (Image img : images) {
                System.out.printf("Image %d: %d\n\n", i, getArea(img));
                i++;
            }
        }
    }

    private static int getArea(Image image) {
        int area = 0;
        /*someCustomObject*/ rows = image.getRows();

        for ( /*someCustomObject*/ row: rows) {
            /**Somehow get spaces*/
        }

        for (/*someCustomObject*/ row: rows) {
            area += /**something*/
        }

        return area;
    }


    private static class Image {

        private List</*someCustomObject*/> _rows;

        Image() {
                _rows = new ArrayList</*someCustomObject*/>();
            }
        void addRow(/*someobject*/ row) {
            _rows.add(row);
        }
         /** Returns the rows in this image.*/
         List</*someobject*/> getRows() {
                return _rows;
            }
    }



}
