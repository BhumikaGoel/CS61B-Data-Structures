
package db61b;


import java.util.HashMap;

/** A collection of Tables, indexed by name.
 *  @author Bhumika Goel */
class Database {
    /** An empty database. */

    public Database() {
        dict = new HashMap<>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {

        if (dict.containsKey(name)) {
            return dict.get(name);
        }
        return null;
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        dict.put(name, table);
    }
    /**The actual "database".
     * Describing how tables are stored as key,value pairs*/
    private static HashMap<String, Table> dict;
}
