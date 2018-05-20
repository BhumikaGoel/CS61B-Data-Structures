package gitlet;

import java.io.Serializable;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.text.SimpleDateFormat;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import java.util.Formatter;
import java.util.Calendar;

import static gitlet.Utils.*;

/** The class Commit.
 *  @author bhumikagoel
 */
public class Commit implements Serializable {

    /**Stores path to branch.*/
    private static String _filePATH = "./.gitlet/files/";

    /** The commit commitMsg. */
    private final String commitMsg;

    /** The SHA-1 string code of the parent commit. */
    private final String prevID;

    /** The SHA-a string code of this commit object. Not the file. */
    private String commitID;

    /** The Date that this object is created. */
    private String commitTime;

    /** The CommitID of the merged-in Commit. */
    private String userParentSha = null;

    /**Format in which date should appear.*/
    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");


    /** Key: the original fileName.
     *  Value: the new fileName derived from SHA-1.
     */
    private HashMap<String, String> oriFileShaFile;

    /** Constructor for the init command; the initialization of gitLet
     *  in a directory. */
    public Commit() {

        commitMsg = "initial commit";
        prevID = null;

        Date epoch = new Date(0L);
        commitTime = dateFormat.format(epoch);

        commitID = sha1("Sentinel ", "Thu Jan 1 00:00:00 1970 +0000");
        oriFileShaFile = new HashMap<>();
    }

    /** Constructor given prev commit's PREVIOUSID
     * and commit msg CMTMSG.*/
    public Commit(String previousID, String cmtMsg) {
        prevID = previousID;
        commitMsg = cmtMsg;
        commonInitializer();
    }

    /** Constructor given prev commit's PREVIOUSID
     * and CMTMSG.
     *  USERID will be merged here. */
    public Commit(String previousID, String userID, String cmtMsg) {
        userParentSha = userID;
        prevID = previousID;
        commitMsg = cmtMsg;
        commonInitializer();
    }

    /**Initializing time instances for new commits.*/
    public void commonInitializer() {
        Calendar calendar = Calendar.getInstance();
        commitTime = dateFormat.format(calendar.getTime());
        commitID = sha1(prevID, commitTime);
        oriFileShaFile = new HashMap<>(copyIntoCommit(prevID).oriFileShaFile);

    }


    /** Returns the commit commitMsg initially passed-in to this object. */
    String getCommitMsg() {
        return commitMsg;
    }

    /** Returns the parent SHA-1 String initially passed-in to this object. */
    String getPrevID() {
        return prevID;
    }

    /** Returns the SHA-1 HashCode for this object. */
    String getCommitID() {
        return commitID;
    }

    /** Returns the CommitTime in milliseconds since the Epoch. */
    String getCommitTime() {
        return commitTime;
    }

    /** Returns a set of all Files under me. */
    Set<String> getCommittedFiles() {
        return oriFileShaFile.keySet();
    }

    /** Returns the sha-1 derived filename from FILENAME.*/
    String getCommittedFileName(String fileName) {
        return oriFileShaFile.get(fileName);
    }

    /** Set oriFileShaFile's FILENAME with a STOREDNAME. */
    void setOriFileShaFile(String fileName, String storedName) {
        if (oriFileShaFile.containsKey(fileName)) {
            oriFileShaFile.replace(fileName, storedName);
        } else {
            oriFileShaFile.put(fileName, storedName);
        }
        return;
    }

    /** Update the STAGE object. */
    void updateStage(Stage stage) {
        String storedFileName;
        for (String fileName : stage.getMarkedForStagingKeys()) {
            if (stage.isFileMarkedForStaging(fileName)) {
                storedFileName = stage.onStageFileName(fileName);
                setOriFileShaFile(fileName, storedFileName);
                stage.moveFileToFilesDir(
                        new File("./.gitlet/stage/" + storedFileName));
            }
        }
        for (String fileName : stage.getMarkedForRemovalKeys()) {
            if (stage.isFileMarkedForRemoval(fileName)) {
                oriFileShaFile.remove(fileName);
            }
        }
        stage.resetAllHashes();
    }

    /** Returns the common Commit between me and OTHER.*/
    Commit commonCommit(Commit other) throws IOException {
        HashSet<String> parentIDs = new HashSet<>();
        parentIDs.add(getCommitID());
        Commit prev = this;
        while (prev.getPrevID() != null) {
            prev = copyIntoCommit(prev.getPrevID());
            parentIDs.add(prev.getCommitID());
        }
        Commit otherPrev = other;
        while (!parentIDs.contains(otherPrev.getCommitID())) {
            otherPrev = copyIntoCommit(otherPrev.getPrevID());
        }
        if (otherPrev == null) {
            throw new FileNotFoundException("Cannot find the split point");
        }
        return otherPrev;
    }

    /** Saving a copy of FILENAME to _filePATH. */
    void saveFile(String fileName) {
        try {
            File newFile = new File(fileName);
            File destFile = new File(String.format(_filePATH + "%s",
                    storedFileName(newFile)));
            writeContents(destFile, readContents(newFile));
        } catch (IllegalArgumentException e) {
            System.out.printf("IAE");
        }
    }

    /** Restores the FILENAME from _filePATH. */
    void restoreFile(String fileName) {
        try {
            File destFile = new File(fileName);
            String from = String.format(_filePATH + "%s",
                    getCommittedFileName(fileName));
            writeContents(destFile, readContents(new File(from)));
        } catch (IllegalArgumentException e) {
            System.out.printf("IAE");
        }
    }

    /** Returns a commit of the given COMMITID. */
    static Commit copyIntoCommit(String commitID) {
        Commit copy = null;
        File f = new File(String.format(
                "./.gitlet/commits/%s.ser", commitID));
        if (f.exists()) {
            try {
                ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(f));
                copy = (Commit) ois.readObject();
                ois.close();
            } catch (IOException e) {
                System.out.println("Could not load your commit.");
            } catch (ClassNotFoundException e) {
                System.out.println("Could not load your commit.");
            }
        }
        return copy;
    }

    /** Stores me in the commit dir. */
    void storeCommit() {
        try {
            File existing = new File(String.format(
                    "./.gitlet/commits/%s.ser", commitID));
            ObjectOutputStream oos =
                    new ObjectOutputStream(new FileOutputStream(existing));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            System.out.println("Could not store commit.");
        }
    }

    /** Checks if Commit C1 and Commit C2 contains same files of
     *  the same contents. Returns true if they do, false otherwise. */
    static boolean areEqual(Commit c1, Commit c2) {
        Set<String> s1 = c1.getCommittedFiles();
        Set<String> s2 = c2.getCommittedFiles();
        if (s1.size() != s2.size()) {
            return false;
        }
        for (String f : s1) {
            if (!c1.getCommittedFileName(f).equals(
                    c2.getCommittedFileName(f))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        Formatter result = new Formatter();
        result.format("===%n");
        result.format("commit %s%n", getCommitID());
        if (userParentSha != null) {
            String shortSha = prevID.substring(0, 7);
            String shortUser = userParentSha.substring(0, 7);
            result.format("Merge: %s %s%n", shortSha, shortUser);
        }
        result.format("Date: %s%n", getCommitTime());
        result.format("%s%n", getCommitMsg());
        return result.toString();
    }
}

