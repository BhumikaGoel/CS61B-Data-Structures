package db61b;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for the DataBase class
 *  @author Bhumika Goel
 */
public class TestDataBase {
    @Test
    public void testDataBase() {

        Database db = new Database();

        String[] columnNames = {"SID", "Lastname", "Firstname",
            "SemEnter", "YearEnter", "Major"};
        Table students = new Table(columnNames);
        db.put("students", students);
        assertTrue(db.get("students") == students);

        String[] columnName = {"CCN", "Num", "Dept", "Time",
            "Room", "Sem", "Year"};
        Table schedule = new Table(columnName);
        db.put("schedule", schedule);
        assertTrue(db.get("schedule") == schedule);

    }
}
