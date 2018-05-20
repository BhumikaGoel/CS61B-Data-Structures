package db61b;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Scanner;

/** Unit tests for CommandInterpreter
 *  @author Bhumika Goel
 */
public class TestCommandInterpreter {
    @Test
    public void testCreateStatement() {
        Scanner input = new Scanner("create table t1 (name, p, a, t);\n"
                + "create table t2 (corgicon);\n" + "t1\n" + "t2\n");
        CommandInterpreter interpreter = new CommandInterpreter(input, null);
        interpreter.createStatement();
        interpreter.createStatement();
        Table t1 = interpreter.tableName();
        Table t2 = interpreter.tableName();

        assertEquals(4, t1.columns());

        assertEquals(String.format("name"), t1.getTitle(0));
        assertEquals(String.format("p"), t1.getTitle(1));
        assertEquals(String.format("a"), t1.getTitle(2));
        assertEquals(String.format("t"), t1.getTitle(3));

        assertEquals(1, t2.columns());
        assertEquals("corgicon", t2.getTitle(0));
    }

    @Test
    public void testInsertStatement() {
        Scanner input = new Scanner("create table table1 (name, p, a, t);\n"
                + "table1\n"
                + "insert into table1 values ('a', 'b', 'c', 'd');\n");
        CommandInterpreter interpreter = new CommandInterpreter(input, null);
        interpreter.createStatement();
        Table table1 = interpreter.tableName();
        interpreter.insertStatement();

        assertEquals(String.format("a"), table1.get(0, 0));
        assertEquals(String.format("b"), table1.get(0, 1));
        assertEquals(String.format("c"), table1.get(0, 2));
        assertEquals(String.format("d"), table1.get(0, 3));
    }

    @Test
    public void testTableDefinition() {
        Scanner input = new Scanner("(name, place, animal, thing)");
        CommandInterpreter interpreter = new CommandInterpreter(input, null);
        Table table1 = interpreter.tableDefinition();

        assertEquals(String.format("name"), table1.getTitle(0));
        assertEquals(String.format("place"), table1.getTitle(1));
        assertEquals(String.format("animal"), table1.getTitle(2));
        assertEquals(String.format("thing"), table1.getTitle(3));
    }

}
