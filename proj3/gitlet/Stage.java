package gitlet;

import java.io.Serializable;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.HashMap;
import java.util.Set;

import static gitlet.Utils.*;


/** The class STAGE is stored under /.gitlet/stage/STAGE.ser.
 *  @author bhumikagoel
 */
public class Stage implements Serializable {

    /**Stores path to stage.*/
    private static String _stagePath = "./.gitlet/stage/";

    /** Key: String fileName,
     * Value: boolean mark. */
    private HashMap<String, Boolean> markedForStaging;

    /** Key: String fileName,
     * Value: boolean mark. */
    private HashMap<String, Boolean> markedForRemoval;

    /** Key: String fileName,
     * Value: String stageName. */
    private HashMap<String, String> fileOnStage;

    /** Commit id of the most recent commit.*/
    private String id;

    /** Constructor. */
    public Stage() {
        markedForStaging = new HashMap<>();
        markedForRemoval = new HashMap<>();
        fileOnStage = new HashMap<>();
        id = "7fedecda468132e9e388e8062758daa7e8ad1ba9";
    }

    /** Returns the Stage object from /.gitlet/STAGE.ser
     *  if it exists.*/
    static Stage copyIntoStage() {
        Stage newStage = null;
        File existing = new File(_stagePath + "STAGE.ser");
        if (existing.exists()) {
            try {
                ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(existing));
                newStage = (Stage) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not return the stage");
            }
        }
        return newStage;
    }

    /** set prev commit id to NEWCOMMITID.*/
    void setCommitID(String newCommitID) {
        id = newCommitID;
    }

    /** Returns the Commit id of the most recent commit. */
    String getCommitID() {
        return id;
    }

    /** Returns the Set of all keys in markedForStaging. */
    Set<String> getMarkedForStagingKeys() {
        return markedForStaging.keySet();
    }

    /** Returns a set of all files in markedForRemoval. */
    Set<String> getMarkedForRemovalKeys() {
        return markedForRemoval.keySet();
    }

    /** Returns a set of all files in markedForStaging. */
    Set<String> getFileOnStageKeys() {
        return markedForStaging.keySet();
    }

    /** Returns if FILENAME is marked for staging. */
    boolean isFileMarkedForStaging(String fileName) {
        if (markedForStaging.containsKey(fileName)) {
            return markedForStaging.get(fileName);
        }
        return false;
    }

    /** Returns if FILENAME is marked for staging. */
    boolean isFileMarkedForRemoval(String fileName) {
        if (markedForRemoval.containsKey(fileName)) {
            return markedForRemoval.get(fileName);
        }
        return false;

    }

    /** Returns the associated file name on stage with FILENAME. */
    String onStageFileName(String fileName) {
        return fileOnStage.get(fileName);
    }

    /** Sets value as MARK for Key FILENAME.*/
    void setMarkedForStaging(String fileName, boolean mark) {
        if (markedForStaging.containsKey(fileName)) {
            markedForStaging.replace(fileName, mark);
        } else {
            markedForStaging.put(fileName, mark);
        }
    }

    /** Sets value as MARK for Key FILENAME.*/
    void setMarkedForRemoval(String fileName, boolean mark) {
        if (markedForRemoval.containsKey(fileName)) {
            markedForRemoval.replace(fileName, mark);
        } else {
            markedForRemoval.put(fileName, mark);
        }
    }

    /** Sets value as STAGENAME for Key FILENAME.*/
    void setFileOnStage(String fileName, String stageName) {
        if (fileOnStage.containsKey(fileName)) {
            fileOnStage.replace(fileName, stageName);
        } else {
            fileOnStage.put(fileName, stageName);
        }
    }

    /** Returns if file is removed from Staging area iff
     * it has not been modified.*/
    boolean updateStagedFiles() {
        Commit c = Commit.copyIntoCommit(id);
        for (String f : getMarkedForStagingKeys()) {
            if (isFileMarkedForStaging(f)
                    && c.getCommittedFiles().contains(f)
                    && storedFileName(new File(f)).equals(
                    c.getCommittedFileName(f))) {
                removeFileFromStage(f);
                return true;
            }
        }
        return false;
    }



    /** Saving a snapshot of FILENAME in stage DIR. */
    void saveFileToStage(String fileName) {
        try {
            File f = new File(fileName);
            File snappedFile = new File(String.format(_stagePath + "%s",
                    storedFileName(f)));
            writeContents(snappedFile, readContents(f));
        } catch (IllegalArgumentException e) {
            System.out.printf("IllegalArgumentException");
        }
    }

    /** Store current stage in given path. */
    void storeStage() {
        try {
            File existing = new File(_stagePath + "STAGE.ser");
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(existing));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            System.out.println("Could not store the stage.");
        }
    }

    /** Deletes all but STAGE.ser files in /.gitlet/stage/ directory. */
    void resetAllHashes() {
        for (File f : new File(_stagePath).listFiles()) {
            if (!f.getName().equals("STAGE.ser")) {
                f.delete();
            }
        }
        markedForStaging.clear();
        markedForRemoval.clear();
        fileOnStage.clear();
    }

    /** Unstage a file from its FILENAME.*/
    void removeFileFromStage(String fileName) {
        if (!markedForStaging.containsKey(fileName)
                || !markedForRemoval.containsKey(fileName)
                || !fileOnStage.containsKey(fileName)) {
            throw new IllegalArgumentException("IAE");
        }

        markedForStaging.remove(fileName);
        markedForRemoval.remove(fileName);
        fileOnStage.remove(fileName);
    }

    /** Returns true if FILENAME is currently staged.*/
    boolean isFileOnStage(String fileName) {
        return fileOnStage.containsKey(fileName);
    }

    /** Returns True if both markedForStaging and markedForRemoval are empty. */
    public boolean isEmpty() {
        return markedForStaging.isEmpty() && markedForRemoval.isEmpty();
    }

    /** Transfers FILENAME to /.gitlet/files/ directory.*/
    void moveFileToFilesDir(File fileName) {
        if (!fileName.exists()) {
            throw new IllegalArgumentException("IAE");
        }
        fileName.renameTo(new File("./.gitlet/files/" + fileName.getName()));
    }
}

