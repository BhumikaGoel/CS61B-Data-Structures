package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.HashMap;
import java.util.Set;

/** The class BRANCH is stored under /.gitlet/HEAD.ser.
 *  @author bhumikagoel
 */
public class Branch implements Serializable {

    /**Stores path to branch.*/
    private static String _pathToBranch = "./.gitlet/HEAD.ser";

    /** Key: branchName
     * Value: commitID.*/
    private final HashMap<String, String> branchHeadMap = new HashMap<>();

    /** The current active branch.*/
    private String _current;

    /** The single Branch object that will be created to keep track of all
     *  HEAds in every branch. */
    public Branch() {
        branchHeadMap.put("master",
                "7fedecda468132e9e388e8062758daa7e8ad1ba9");
        _current = "master";
    }

    /** Returns the current branch. */
    String getCurrentBranch() {
        return _current;
    }

    /** Returns a set of all branches. */
    Set<String> getAllBranches() {
        return branchHeadMap.keySet();
    }

    /** Returns the BRANCHNAME's Head. */
    Commit getHead(String branchName) {
        return Commit.copyIntoCommit(branchHeadMap.get(branchName));
    }

    /** Returns the BRANCH's Head's CommitID. */
    String getHeadCommitID(String branch) {
        return branchHeadMap.get(branch);
    }

    /** Replaces BRANCHNAME's head with new COMMITID.*/
    void setHead(String branchName, String commitID) {
        if (branchHeadMap.containsKey(branchName)) {
            branchHeadMap.replace(branchName, commitID);
        } else {
            branchHeadMap.put(branchName, commitID);
        }
    }

    /** Removes the BRANCHNAME..*/
    void removeBranch(String branchName) {
        if (branchHeadMap.containsKey(branchName)) {
            branchHeadMap.remove(branchName);
        } else {
            throw new IllegalArgumentException(branchName
                    + " not found");
        }
    }

    /** Returns a new Branch object
     * from /.gitlet/commits/commitID.ser. */
    static Branch copyIntoBranch() {
        Branch result = null;
        File existing = new File(_pathToBranch);
        if (existing.exists()) {
            try {
                ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(existing));
                result = (Branch) ois.readObject();
                ois.close();
            } catch (IOException e) {
                System.out.println("IOW");
            } catch (ClassNotFoundException e) {
                System.out.println("CNFE");
            }
        }
        return result;
    }

    /** Store current Branch in /.gitlet/HEAD.ser. */
    void storeBranch() {
        try {
            File existing = new File(_pathToBranch);
            ObjectOutputStream oooooooos =
                    new ObjectOutputStream(new FileOutputStream(existing));
            oooooooos.writeObject(this);
            oooooooos.close();
        } catch (IOException e) {
            System.out.println("IOE");
        }
    }

    /** Update currBranch to BRANCHNAME.*/
    void setCurrentBranch(String branchName) {
        if (branchHeadMap.containsKey(branchName)) {
            _current = branchName;
        } else {
            throw new IllegalArgumentException(branchName
                    + " is illegal.");
        }
    }
}
