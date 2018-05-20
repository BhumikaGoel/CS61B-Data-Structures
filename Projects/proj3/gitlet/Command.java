package gitlet;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Formatter;
import java.util.Set;

import static gitlet.Utils.*;

/**Public class Command. Delegates and performs
 * the main functions in a simplified version
 * of git.
 * @author bhumikagoel */

public class Command {

    /**Saves arguments passed into command line.*/
    private String[] _args;

    /**Saves the path to the main repo.*/
    private static String _repoPath = "./.gitlet/";

    /**Set ARGS to a field.*/
    public void setArgs(String[] args) {
        _args = args;
        delegate(_args);
    }

    /** Check if number of ARGS to the mainCommand are correct. */
    static void incorrectOperands(String[] args) {
        int len = args.length;
        if (len == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String mainCommand = args[0];
        if (!mainCommand.equals("init")) {
            File repo = new File(_repoPath);
            if (!repo.exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
        }
        switch (mainCommand) {
        case "init": case "log": case "global-log": case "status":
            if (len != 1) {
                System.out.println("Incorrect operands.");
                return;
            }
            break;
        case "add": case "rm": case "find": case "branch":
        case "rm-branch": case "reset": case "merge": case "delete":
        case "commit":
            if (len != 2) {
                System.out.println("Incorrect operands.");
                return;
            }
            break;
        case "checkout":
            if (len < 2 || len > 4) {
                System.out.println("Incorrect operands.");
                return;
            } else if (len == 3 && !args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            } else if (len == 4 && !args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            }
            break;
        default:
            break;
        }
    }

    /**Delegate function calls depending upon ARGS.*/
    public void delegate(String[] args) {
        incorrectOperands(_args);
        String mainCommand = _args[0];
        switch (mainCommand) {
        case "init":
            init();
            break;
        case "add":
            add(_args[1]);
            break;
        case "commit":
            commit(_args[1]);
            break;
        case "rm":
            remove(_args[1]);
            break;
        case "log":
            printLog();
            break;
        case "global-log":
            printGlobalLog();
            break;
        case "find":
            find(_args[1]);
            break;
        case "status":
            status();
            break;
        case "checkout":
            switch (_args.length) {
            case 2:
                checkOutBranch(_args[1]);
                break;
            case 3:
                checkOutFile(_args[2]);
                break;
            case 4:
                checkOut(_args[1], _args[3]);
                break;
            default:
                throw new IllegalArgumentException("IAE");
            }
            break;
        case "branch":
            branch(_args[1]);
            break;
        case "rm-branch":
            removeBranch(_args[1]);
            break;
        case "reset":
            reset(_args[1]);
            break;
        case "merge":
            merge(_args[1]);
            break;
        default:
            System.out.println("No command with that name exists.");
            break;
        }
    }

    /** Initializes git repo. */
    static void init() {
        File initFile = new File(_repoPath);
        if (initFile.exists()) {
            System.out.println("A Gitlet version-control"
                    + " system already exists in the current directory.");
            return;
        }
        initFile.mkdir();

        File filesDir = new File(_repoPath + "files/");
        filesDir.mkdir();

        File commitsDir = new File(_repoPath + "commits/");
        commitsDir.mkdir();

        File stageDir = new File(_repoPath + "stage/");
        stageDir.mkdir();

        new Commit().storeCommit();
        new Branch().storeBranch();
        new Stage().storeStage();
    }

    /**Helper function to avoid code redundancy.
     * Takes in STAGE, BRANCH, HEAD. */
    private static void helperInitializer(Stage stage,
                                          Branch branch, Commit head) {
        stage = Stage.copyIntoStage();
        branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        head = branch.getHead(currentBranch);

    }

    /** Add FILENAME to stage. */
    private static void add(String fileName) {

        File newFile = new File(fileName);

        if (!newFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        Stage stage = Stage.copyIntoStage();
        Branch branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        Commit head = branch.getHead(currentBranch);

        String storedFileName;

        if (head.getCommittedFiles().contains(fileName)) {
            storedFileName = head.getCommittedFileName(fileName);
            if (filesAreEqual(newFile,
                    new File("./.gitlet/files/" + storedFileName))
                    && fileName.equals(storedFileName.substring
                    (UID_LENGTH + 2))) {
                if (stage.isFileOnStage(fileName)) {
                    stage.removeFileFromStage(fileName);
                    stage.storeStage();
                }
                return;
            }
        }

        stage.setMarkedForStaging(fileName, true);
        stage.setMarkedForRemoval(fileName, false);
        stage.saveFileToStage(fileName);
        stage.setFileOnStage(fileName, storedFileName(new File(fileName)));
        stage.storeStage();
    }

    /** Commits the added filename with COMMITMSG. */
    static void commit(String commitMsg) {
        if (commitMsg == null || commitMsg.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }

        Stage stage = Stage.copyIntoStage();
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        Branch branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        Commit head = branch.getHead(currentBranch);


        Commit node = new Commit(head.getCommitID(), commitMsg);
        String c = node.getCommitID();
        node.updateStage(stage);
        stage.setCommitID(c);
        stage.storeStage();
        node.storeCommit();
        branch.setHead(currentBranch, c);
        branch.storeBranch();
    }

    /** Remove a FILENAME from staging area. */
    static void remove(String fileName) {

        Stage stage = Stage.copyIntoStage();
        Branch branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        Commit head = branch.getHead(currentBranch);

        boolean fileIsStaged = false;
        boolean isStaged = false;
        boolean isTracked = false;
        if (stage.isFileMarkedForStaging(fileName)) {
            isStaged = true;
            stage.removeFileFromStage(fileName);
            fileIsStaged = true;
            stage.storeStage();
        }

        String comittedFileName = head.getCommittedFileName(
                fileName);
        if (comittedFileName != null) {
            if (fileIsStaged) {
                stage = Stage.copyIntoStage();
            }

            isTracked = true;
            stage.setMarkedForStaging(fileName, false);
            stage.setFileOnStage(fileName, comittedFileName);
            stage.setMarkedForRemoval(fileName, true);
            restrictedDelete(fileName);
            stage.storeStage();
        }
        if (!isStaged && !isTracked) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /** Prints log of active branch.*/
    static void printLog() {

        Branch branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        String cID = branch.getHeadCommitID(currentBranch);
        Commit c = Commit.copyIntoCommit(cID);
        System.out.println(c);
        cID = c.getPrevID();

        while (cID != null) {
            c = Commit.copyIntoCommit(cID);
            System.out.println(c);
            cID = c.getPrevID();
        }

    }

    /** Prints over all log of all listed files.*/
    static void printGlobalLog() {
        File commitDIR = new File(_repoPath + "commits/");
        for (File f : commitDIR.listFiles()) {
            System.out.println(Commit.copyIntoCommit(
                  f.getName().substring(0, UID_LENGTH)));
        }
    }

    /** Find a file associated with COMMITMSG. */
    static void find(String commitMsg) {
        boolean found = false;
        File commitDIR = new File(_repoPath + "commits/");
        Commit cmt;
        for (File f : commitDIR.listFiles()) {
            cmt = Commit.copyIntoCommit(f.getName().substring(0, UID_LENGTH));
            if (cmt.getCommitMsg().equals(commitMsg)) {
                found = true;
                System.out.println(f.getName().substring(0, UID_LENGTH));
            }
        }

        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Prints status of the current repository.*/
    static void status() {
        Formatter f = new Formatter();

        f.format("=== Branches ===%n");
        Branch branch = Branch.copyIntoBranch();
        List<String> allBranches = sortList(branch.getAllBranches());
        for (String b : allBranches) {
            if (branch.getCurrentBranch().equals(b)) {
                f.format("*");
            }
            f.format("%s%n", b);
        }
        f.format("%n");

        Stage stage = Stage.copyIntoStage();
        f.format("=== Staged Files ===%n");
        List<String> lst = sortList(stage.getMarkedForStagingKeys());
        for (String fileName : lst) {
            if (stage.isFileMarkedForStaging(fileName)) {
                f.format("%s%n", fileName);
            }
        }
        f.format("%n");

        f.format("=== Removed Files ===%n");
        List<String> lst2 = sortList(stage.getMarkedForRemovalKeys());
        for (String fileName : lst2) {
            if (stage.isFileMarkedForRemoval(fileName)) {
                f.format("%s%n", fileName);
            }
        }
        f.format("%n");

        f.format("=== Modifications Not Staged For Commit ===%n");
        f.format("%n");

        f.format("=== Untracked Files ===%n");
        f.format("%n");

        System.out.print(f.toString());
    }

    /** Checkout the given FILENAME. */
    static void checkOutFile(String fileName) {

        Branch branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        Commit head = branch.getHead(currentBranch);
        if (!head.getCommittedFiles().contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        head.restoreFile(fileName);
    }

    /** Checkout FILENAME at given COMMITID. */
    static void checkOut(String commitID, String fileName) {

        boolean found = false;
        String commitPath = "./.gitlet/commits/";
        if (commitID.length() < UID_LENGTH) {
            for (File f : new File(commitPath).listFiles()) {
                if (isSubstring(f.getName(), commitID)) {
                    commitID = f.getName().substring(0, UID_LENGTH);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        }

        Commit c = Commit.copyIntoCommit(commitID);
        if (c == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (!c.getCommittedFiles().contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        c.restoreFile(fileName);
    }

    /**Returns a boolean.
     * Helper function to avoid redundancy of code using COMMITID.*/
    static boolean checkoutResetID(String commitID) {
        boolean found = false;
        String commitPath = "./.gitlet/commits/";
        if (commitID.length() < UID_LENGTH) {
            for (File f : new File(commitPath).listFiles()) {
                if (isSubstring(f.getName(), commitID)) {
                    commitID = f.getName().substring(0, UID_LENGTH);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No commit with that id exists.");
                return found;
            }
        }
        return found;

    }

    /** Checks for presence of any untracked file among
     * OTHERFILES and CURRENTFILES. */
    static void isFileUntracked(Set<String> otherFiles,
                                Set<String> currentFiles) {
        for (String f : otherFiles) {
            if (new File(f).exists()
                    && !currentFiles.contains(f)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                return;
            }
        }
    }

    /** Checkout a branch with BRANCHNAME. */
    static void checkOutBranch(String branchName) {

        Branch branch = Branch.copyIntoBranch();
        String currentBranch = branch.getCurrentBranch();
        if (currentBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        if (!branch.getAllBranches().contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }

        Commit c = branch.getHead(currentBranch);
        Set<String> cFiles = c.getCommittedFiles();

        Commit head = branch.getHead(branchName);
        Set<String> headFiles =
                head.getCommittedFiles();
        isFileUntracked(headFiles, cFiles);
        for (String f : headFiles) {
            head.restoreFile(f);
        }
        branch.setCurrentBranch(branchName);
        for (String f : cFiles) {
            if (!headFiles.contains(f)) {
                restrictedDelete(f);
            }
        }
        branch.storeBranch();
        Stage stage = Stage.copyIntoStage();
        stage.resetAllHashes();
        stage.storeStage();
    }

    /** Creates new branch with given BRANCHNAME. */
    static void branch(String branchName) {

        Branch branch = Branch.copyIntoBranch();
        if (branch.getAllBranches().contains(branchName)) {
            System.out.println("branch with that name already exists.");
            return;
        }
        String headID =
                branch.getHeadCommitID(branch.getCurrentBranch());
        branch.setHead(branchName, headID);
        branch.storeBranch();
    }

    /** Remove the given BRANCHNAME.*/
    static void removeBranch(String branchName) {

        Branch branch = Branch.copyIntoBranch();

        if (branchName.equals(branch.getCurrentBranch())) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!branch.getAllBranches().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        branch.removeBranch(branchName);
        branch.storeBranch();
    }

    /** Reset using COMMITID. */
    static void reset(String commitID) {
        boolean found = false;
        String commitPath = "./.gitlet/commits/";
        if (commitID.length() < UID_LENGTH) {
            for (File f : new File(commitPath).listFiles()) {
                if (isSubstring(f.getName(), commitID)) {
                    commitID = f.getName().substring(0, UID_LENGTH);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        Branch branch = Branch.copyIntoBranch();

        Commit c = Commit.copyIntoCommit(commitID);
        if (c == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Set<String> cFiles = c.getCommittedFiles();

        Commit head = branch.getHead(branch.getCurrentBranch());
        Set<String> headFiles = head.getCommittedFiles();
        isFileUntracked(cFiles, headFiles);
        for (String f : cFiles) {
            c.restoreFile(f);
        }
        for (String f : headFiles) {
            if (!cFiles.contains(f)) {
                restrictedDelete(f);
            }
        }

        branch.setHead(branch.getCurrentBranch(), commitID);
        branch.storeBranch();

        Stage stage = Stage.copyIntoStage();
        stage.resetAllHashes();
        stage.storeStage();
    }

    /**Helper to store BRANCH and update stage.*/
    static void helperStageBranch(Branch branch) {
        branch.storeBranch();
        Stage stage = Stage.copyIntoStage();
        stage.resetAllHashes();
        stage.storeStage();

    }

    /** Merge BRANCHNAME. */
    static void merge(String branchName) {

        Stage stage = Stage.copyIntoStage();
        if (!stage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        Branch branch = Branch.copyIntoBranch();
        if (!branch.getAllBranches().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String current = branch.getCurrentBranch();
        if (current.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Commit cHead, oHead, common;
        try {
            cHead = branch.getHead(current);
            oHead = branch.getHead(branchName);

            common = cHead.commonCommit(oHead);

            if (common.getCommitID().equals(oHead.getCommitID())) {
                System.out.println("Given branch is an ancestor"
                        + " of the current branch.");
                return;
            }
            if (common.getCommitID().equals(cHead.getCommitID())) {
                branch.setHead(current, oHead.getCommitID());
                branch.storeBranch();
                System.out.println("Current branch fast-forwarded.");
                return;
            }

            merge(cHead, oHead, common);

            merge(cHead, oHead, branch, current, branchName);
        } catch (IOException ioe) {
            System.out.println("Could not merge properly.");
            return;
        }
    }

    /** Merge overloading with CHEAD, OHEAD, COMMON. */
    static void merge(Commit chead, Commit oHead, Commit common)
            throws FileNotFoundException {

        Set<String> cFiles = chead.getCommittedFiles();
        Set<String> oFiles = oHead.getCommittedFiles();

        boolean inConflict = false;

        isFileUntracked(oFiles, cFiles);
        for (String f : oFiles) {
            String cFileName = chead.getCommittedFileName(f);
            String oFileName = oHead.getCommittedFileName(f);
            String commonFileName = common.getCommittedFileName(f);

            if (commonFileName == null) {
                if (cFileName == null) {
                    checkOut(oHead.getCommitID(), f);
                    add(f);
                } else if (cFileName != null
                        && !cFileName.equals(oFileName)) {
                    inConflict = true;
                    writeConflictedFile(f, chead, oHead);
                    add(f);
                }
            } else {
                continue;
            }

        }
        merge(common.getCommittedFiles(), chead, oHead,
                common, oHead.getCommitID(), inConflict);
    }

    /**To perform functions that may result in code repetition.
     * Takes in FILE, CHEAD, OHEAD.*/
    static void helperMerge(String file, Commit cHead,
                            Commit oHead) throws FileNotFoundException {
        writeConflictedFile(file, cHead, oHead);
        add(file);
    }

    /** Merge overriding with COMMONFILES, CHEAD,
     * OHEAD,COMMON,OCOMMITID, INCONFLICT.*/
    static void merge(Set<String> commonFiles, Commit cHead,
                      Commit oHead, Commit common, String oCommitID,
                      boolean inConflict) throws FileNotFoundException {

        for (String f : commonFiles) {
            String oFile = oHead.getCommittedFileName(f);
            String cFile = cHead.getCommittedFileName(f);
            String commonFile = common.getCommittedFileName(f);
            if (cFile != null && oFile != null) {
                if (cFile.equals(commonFile) && !oFile.equals(commonFile)) {
                    checkOut(oCommitID, f);
                    add(f);

                } else if (!cFile.equals(oFile)) {
                    inConflict = true;
                    helperMerge(f, cHead, oHead);
                } else  {
                    continue;
                }

            } else if (cFile != null && oFile == null) {
                if (cFile.equals(commonFile)) {
                    restrictedDelete(f);
                } else {
                    inConflict = true;
                    helperMerge(f, cHead, oHead);
                }

            } else if (cFile == null && oFile != null) {
                if (!oFile.equals(commonFile)) {
                    inConflict = true;
                    helperMerge(f, cHead, oHead);
                } else {
                    continue;
                }

            } else {
                continue;
            }
        }

        if (inConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Merge overloading with CHEAD, OHEAD, BRANCH, CBRANCH, OBRANCH.*/
    static void merge(Commit cHead, Commit oHead, Branch branch,
                                String cBranch, String oBranch) {

        Stage stage = Stage.copyIntoStage();
        String commitMsg = String.format("Merged %s into %s.",
                oBranch, cBranch);
        Commit mergeCommit = new Commit(cHead.getCommitID(),
                oHead.getCommitID(), commitMsg);
        mergeCommit.updateStage(stage);

        if (Commit.areEqual(mergeCommit, cHead)) {
            System.out.println("No changes added to the commit.");
            return;
        }
        stage.setCommitID(mergeCommit.getCommitID());
        stage.storeStage();
        mergeCommit.storeCommit();
        branch.setHead(cBranch, mergeCommit.getCommitID());
        branch.storeBranch();
    }



    /** Write into FILENAME when CURRENT and OTHER are in conflict. */
    static void writeConflictedFile(String fileName, Commit current,
                                    Commit other)
            throws FileNotFoundException {
        File fCurrent = new File("./.gitlet/files/"
                + current.getCommittedFileName(fileName));
        File fOther = new File("./.gitlet/files/"
                + other.getCommittedFileName(fileName));
        File fNew = new File(fileName);
        if (!fCurrent.exists() && !fOther.exists()) {
            throw new FileNotFoundException("Conflicted files not found.");
        }
        byte[] line = "\n".getBytes(StandardCharsets.UTF_8);
        byte[] bCurrent, bOther;
        if (fCurrent.exists()) {
            bCurrent = readContents(fCurrent);
        } else {
            bCurrent = new byte[] {};
        }
        if (fOther.exists()) {
            bOther = readContents(fOther);
        } else {
            bOther = new byte[] {};
        }
        writeContents(fNew, "<<<<<<< HEAD", line,
                bCurrent, "=======", line,
                bOther, ">>>>>>>", line);
    }

}


