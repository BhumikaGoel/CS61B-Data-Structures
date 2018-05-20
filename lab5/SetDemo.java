import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetDemo {

    public static void main (String args[]) {
        Set <String> s = new HashSet <String>();
        s.add("papa");
        s.add("bear");
        s.add("mama");
        s.add("bear");
        s.add("baby");
        s.add("bear");

        System.out.println(s);
    }

}
