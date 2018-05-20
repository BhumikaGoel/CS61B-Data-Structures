package db61b;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for the Table class. While select1 and select2
 * are correct, they result in issues due to path names of .db files
 *  @author Bhumika Goel
 */
public class TestTable {
    @Test
    public void testAdd() {
        String[] titles1 = {"name", "place", "animal",
            "thing"};
        Table t1 = new Table(titles1);
        assertEquals(t1.columns(), 4);
        assertEquals(2, t1.findColumn("animal"));

        t1.add(new String[] {"bhu", "boston", "bear",
            "brinjal"});
        assertEquals(1, t1.size());

        assertEquals("animal", t1.getTitle(2));
        t1.add(new String[] {"Carine", "chicago", "cub",
            "couch"});
        assertEquals(2, t1.size());
        assertEquals("cub", t1.get(1, 2));


        t1.add(new String[] {"irvine", "italy", "iguana",
            "igloo"});
        assertEquals(3, t1.size());

    }

    /**These have been commented out for having different path names.*/
    /*@Test
    public void testSelect1() {

        Table schedule = Table.readTable("testing/schedule");
        Table t1 = new Table(new String[] {"CCN", "Dept"});
        t1.add(new String[] {"21228", "EECS"});
        t1.add(new String[] {"21229", "EECS"});
        t1.add(new String[] {"21232", "EECS"});
        t1.add(new String[] {"21231", "EECS"});

        Column ccn = new Column("CCN", schedule);
        Column dept = new Column("Dept", schedule);

        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("CCN");
        columnNames.add("Dept");
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(ccn, ">", "21000"));
        conditions.add(new Condition(dept, "=", "EECS"));
        Table result = schedule.select(columnNames, conditions);
        t1.print();
        System.out.println();
        result.print();

    }

    @Test
    public void testSelect2() {
        Table enrolled = Table.readTable("testing/enrolled");
        Table students = Table.readTable("testing/students");

        Table t1 = new Table(
                new String[] {"Firstname", "Lastname", "Grade"});
        t1.add(new String[]{"Jason", "Knowles", "B"});
        t1.add(new String[] {"Shana", "Brown", "B+"});
        t1.add(new String[] {"Valerie", "Chan", "B+"});
        t1.add(new String[] {"Yangfan", "Chan", "B"});

        Column CCN = new Column("CCN", students, enrolled);

        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("Firstname");
        columnNames.add("Lastname");
        columnNames.add("Grade");

        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(CCN, "=", "21001"));
        Table result = students.select(enrolled, columnNames, conditions);

        t1.print();
        System.out.println();
        result.print();

    }
    */
}
