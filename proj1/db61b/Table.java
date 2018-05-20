package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;
        _columns = new ValueList[_rowSize];

        for (int i = 0; i < _rowSize; i++) {
            _columns[i] = new ValueList();
        }

    }

    /** A new Table whose columns are given by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _rowSize;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {

        for (int i = 0; i < _rowSize; i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        return _size;
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {

            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {

        if (values.length != columns()) {
            return false;
        }

        for (int r = 0; r < size(); r++) {
            String [] currow = new String[columns()];
            for (int c = 0; c < columns(); c++) {
                currow[c] = this.get(r, c);
            }
            if (Arrays.equals(currow, values)) {
                return false;
            }
        }

        for (int i = 0; i < columns(); i++) {
            _columns[i].add(values[i]);
        }

        _size += 1;

        int comp, destination = 0;
        int last = size() - 1;
        int i;
        int saved = -1;
        for (i = 0; i < _index.size(); i++) {
            comp = compareRows(last, _index.get(i));
            if (comp < 0) {
                destination = i;
                break;
            }
            if ((i == _index.size() - 1) && (comp > 0)) {
                saved = i;
                _index.add(last);
                break;
            }
        }

        if (saved == -1) {
            _index.add(destination, last);
        }

        return true;

    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {

        String[] rowvalues = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            rowvalues[i] = columns.get(i).getFrom(rows);
        }

        return this.add(rowvalues);

    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");

            String ptr = input.readLine();
            table = new Table(columnNames);
            while (ptr != null) {
                String[] data = ptr.split(",");
                table.add(data);
                ptr = input.readLine();
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep = "";
            output = new PrintStream(name + ".db");

            for (int i = 0; i < columns(); i++) {
                if (i == columns() - 1) {
                    output.print(getTitle(i));
                } else {
                    output.print(getTitle(i) + ",");
                }
            }

            for (int i = 0; i < size(); i++) {
                output.println();
                for (int j = 0; j < columns(); j++) {
                    if (j == columns() - 1) {
                        output.print(get(i, j));
                    } else {
                        output.print(get(i, j) + ",");
                    }
                }
            }

        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {

        for (int r: _index) {

            String sentence = "  ";
            for (int c = 0; c < columns(); c++) {
                sentence = sentence + " " + this.get(r, c);
            }
            System.out.println(sentence);
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);

        for (int r = 0; r < _size; r++) {
            ArrayList<String> sentence = new ArrayList<String>();
            if (Condition.test(conditions, r)) {
                for (String col: columnNames) {
                    sentence.add((new Column(col, this)).getFrom(r));
                }
                String[] rowvalues = new String[sentence.size()];
                rowvalues = sentence.toArray(rowvalues);
                result.add(rowvalues);
            }

        }

        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);

        ArrayList<Column> allColumnNames = new ArrayList<Column>();
        ArrayList<Column> colsOfTable1 = new ArrayList<Column>();
        ArrayList<Column> colsOfTable2 = new ArrayList<Column>();

        for (int i = 0; i < columnNames.size(); i++) {
            allColumnNames.add(new Column(columnNames.get(i), this, table2));
        }

        for (int c1 = 0; c1 < _titles.length; c1++) {
            int c = table2.findColumn(getTitle(c1));
            if (c > -1) {
                colsOfTable1.add(new Column(getTitle(c1), this));
                colsOfTable2.add(new Column(table2.getTitle(c), table2));
            }
        }

        for (int r1 = 0; r1 < size(); r1++) {
            for (int r2 = 0; r2 < table2.size(); r2++) {
                if ((equijoin(colsOfTable1, colsOfTable2, r1, r2))
                        && (Condition.test(conditions, r1, r2))) {
                    result.add(allColumnNames, r1, r2);
                }
            }
        }

        return result;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        for (int i = 0; i < common1.size(); i++) {
            String str1 = common1.get(i).getFrom(row1);
            String str2 = common2.get(i).getFrom(row2);
            if (!str1.equals(str2)) {
                return false;
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
