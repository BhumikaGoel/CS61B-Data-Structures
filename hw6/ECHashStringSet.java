import java.util.LinkedList;
//import java.util.ArrayList;

/** A set of String values.
 *  @author BHUMIKA GOEL
 */


class ECHashStringSet extends BSTStringSet {
    private static double minLoad = 0.2;
    private static double maxLoad = 5;
    private LinkedList<String>[] buckets;
    private int size;
    private int sizecopy;


    public ECHashStringSet() {
        size = 0;
        sizecopy = 0;
        int bucketSize = (int) (1/minLoad);
        buckets = new LinkedList[bucketSize];
        for (int i = 0; i < bucketSize; i += 1) {
            buckets[i] = new LinkedList<String>();
        }
    }

    @Override
    public void put(String s) {
        size++;
        //if(s != null)
        //{
        if(currLoad() > maxLoad) {
            resize();
            size = sizecopy;
        }

        int hashVal = hashToStore(s.hashCode());
        //if(buckets[hashVal] == null) {
          //  buckets[hashVal] = new LinkedList<String>();
        //}
        LinkedList<String> temp = buckets[hashVal];
        if (!temp.contains(s)) {
            temp.add(s);
        }

        size++;
        sizecopy++;


        //}
    }

    private void resize(){
        LinkedList<String>[] temp = buckets;
        //ECHashStringSet temp = new ECHashStringSet();
        buckets = new LinkedList[2*temp.length];
        size = 0;
        sizecopy = 0;
        for (int i = 0; i < buckets.length; i += 1) {
            buckets[i] = new LinkedList<String>();
        }

        for(LinkedList<String> lst : temp)
            if(lst != null)
                for(String e : lst)
                    this.put(e);

    }

    private double currLoad(){
        double load = ((double)size)/((double)buckets.length);
        return load;
    }

    private int hashToStore(int hashCode){

        int unsigHash =  hashCode & 0x7ffffff;
        return unsigHash % buckets.length;
    }

}


