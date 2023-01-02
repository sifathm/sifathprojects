package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SomeObj implements Serializable {

    private Boolean ifConflict = false;

    public void init() {

        // Check if the user has already initiated a gitlet repo before.
        if (Repository.checkExistence()) {
            // Set up repositories.
            Repository.setupPersistence();
            // Create and save initial commit.
            Commit initial = new Commit("initial commit", null, new HashMap<String, String>());
            initial.setID(initial);
            saveCommit(initial);
            // Point the main pointer to the new commit,
            // save it and record it as the HEAD pointer in the HEAD.txt.
            saveBranch(initial.getID(), "main");
            File headFile = new File(Repository.HEAD + "/HEAD.txt");
            Utils.writeContents(headFile, "main");
            // Initiate and save the staging area.
            StagingArea addStage = new StagingArea();
            StagingArea removeStage = new StagingArea();
            saveStage(addStage, "addStage");
            saveStage(removeStage, "removeStage");
        } else {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }
    }

    public void add(String fileName) {
        File original = new File(Repository.CWD + "/" + fileName);
        // check if there's such a file.
        if (!original.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Commit currCommit = getCurrentCommit();
        // read the content from the original file, copy it into a new blob and save it.
        String content = Utils.readContentsAsString(original);
        StagingArea removeStage = getCurrentStage("removeStage");
        ArrayList<String> removedFilNames = new ArrayList<>(removeStage.getStagedFiles().keySet());
        if (removedFilNames.contains(fileName)) {
            removeStage.remove(fileName);
            saveStage(removeStage, "removeStage");
            return;
        }
        String currentCommittedVersion = currCommit.getLinkedBlob().get(fileName);
        if (currCommit.getLinkedBlob().get(fileName) != null
                && currentCommittedVersion.equals(Utils.sha1(content))) {
            return;
        }
        Blob newBlob = new Blob(content);
        saveBlob(newBlob);
        // load the current staging area, add the files to the area and save it.
        StagingArea addStage = getCurrentStage("addStage");
        addStage.add(fileName, newBlob.getID());
        saveStage(addStage, "addStage");
    }


    /**
     * Learned how to make copies of hashmap from @CSDN
     */
    public void commit(String message) {
        // load the staging area, check if there's any added files
        // and if the user has made any message input.
        StagingArea addStage = getCurrentStage("addStage");
        StagingArea removeStage = getCurrentStage("removeStage");
        if (addStage.getStagedFiles().isEmpty() && removeStage.getStagedFiles().isEmpty()) {
            System.out.println("No changes added to the commit.");
        }
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
        }
        Commit currCommit = getCurrentCommit();
        // save all the added but untracked files into a new commit and save the commit.
        HashMap<String, String> trackedBlobs = (HashMap<String, String>)
                currCommit.getLinkedBlob().clone();
        ArrayList<String> filesToCommit = new ArrayList(addStage.getStagedFiles().keySet());
        ArrayList<String> filesToRemove = new ArrayList(removeStage.getStagedFiles().keySet());
        for (String filename : filesToRemove) {
            removeStage.remove(filename);
            trackedBlobs.remove(filename);
        }
        for (String filename : filesToCommit) {
            trackedBlobs.put(filename, addStage.getStagedFiles().get(filename));
        }
        Commit newCommit = new Commit(message, currCommit.getID(), trackedBlobs);
        newCommit.setID(newCommit);
        saveCommit(newCommit);
        // update the HEAD pointer, clear the staging area and save it.
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String branchName = Utils.readContentsAsString(headFile);
        saveBranch(newCommit.getID(), branchName);
        addStage.clearStage();
        removeStage.clearStage();
        saveStage(addStage, "addStage");
        saveStage(removeStage, "removeStage");
    }


    public void log() {
        Commit currCommit = getCurrentCommit();
        // go to the parent of the current commit if it still has a parent.
        while (currCommit.getParent() != null) {
            System.out.println("===");
            System.out.println("commit " + currCommit.getID());
            System.out.println("Date: " + currCommit.getTimestamp());
            System.out.println(currCommit.getMessage());
            System.out.println();
            currCommit = getParentCommit(currCommit);
        }
        // return the initial commit.
        Commit initial = new Commit("initial commit", null, new HashMap<String, String>());
        initial.setID(initial);
        System.out.println("===");
        System.out.println("commit " + initial.getID());
        System.out.println("Date: " + initial.getTimestamp());
        System.out.println(initial.getMessage());
    }

    public void gl() {
        ArrayList<String> commitList = new ArrayList<>(Utils.plainFilenamesIn(Repository.COMMITS));
        for (String commitName : commitList) {
            File thisCommitFile = new File(Repository.COMMITS + "/" + commitName);
            Commit thisCommit = Utils.readObject(thisCommitFile, Commit.class);
            System.out.println("===");
            System.out.println("commit " + thisCommit.getID());
            System.out.println("Date: " + thisCommit.getTimestamp());
            System.out.println(thisCommit.getMessage());
        }
    }

    public void find(String commitMessage) {
        ArrayList<String> commitList = new ArrayList<>(Utils.plainFilenamesIn(Repository.COMMITS));
        int counter = 0;
        for (String commit : commitList) {
            File thisCommitFile = new File(Repository.COMMITS + "/" + commit);
            Commit thisCommit = Utils.readObject(thisCommitFile, Commit.class);
            if (thisCommit.getMessage().equals(commitMessage)) {
                counter += 1;
                System.out.println(thisCommit.getID());
            }
        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
        }

    }

    //checkout -- [file name]
    public void checkout1(String toCheckout) {

        Commit currCommit = getCurrentCommit();
        HashMap<String, String> linkedBlobs = currCommit.getLinkedBlob();
        // check if the file has been committed.
        if (!linkedBlobs.containsKey(toCheckout)) {
            System.out.println("File does not exist in that commit.");
        }
        // if the file already exists in the CWD, then delete it.
        File f = new File(Repository.CWD + "/" + toCheckout);
        if (f.exists()) {
            f.delete();
        }
        // get the content of file committed and write it to a new file in the CWD.
        File blobFile = new File(Repository.BLOBS
                + "/" + currCommit.getLinkedBlob().get(toCheckout) + ".txt");
        Blob blob = Utils.readObject(blobFile, Blob.class);
        File newFile = new File(Repository.CWD + "/" + toCheckout);
        Utils.writeContents(newFile, blob.getContent());
    }

    //checkout --[commit id] [file name]
    //similar logic with checkout1
    public void checkout2(String commitID, String toCheckout, String operand) {
        if (operand.equals("--")) {
            File f = new File(Repository.CWD + "/" + toCheckout);
            List<String> commitList = Utils.plainFilenamesIn(Repository.COMMITS);
            HashMap<String, String> shortCommitMap = new HashMap<>();
            for (String str : commitList) {
                shortCommitMap.put(str.substring(0, 8), str);
            }
            if (!commitList.contains(commitID + ".txt") && !shortCommitMap.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                return;
            }
            if (commitID.length() == 8) {
                String trueID = shortCommitMap.get(commitID);
                File commitFile = new File(Repository.COMMITS + "/" + trueID);
                Commit currCommit = Utils.readObject(commitFile, Commit.class);
                HashMap<String, String> linkedBlobs = currCommit.getLinkedBlob();
                if (linkedBlobs.containsKey(toCheckout)) {
                    if (f.exists()) {
                        f.delete();
                    }
                    File blobFile = new File(Repository.BLOBS + "/"
                            + currCommit.getLinkedBlob().get(toCheckout) + ".txt");
                    Blob blob = Utils.readObject(blobFile, Blob.class);
                    File newFile = new File(Repository.CWD + "/" + toCheckout);
                    Utils.writeContents(newFile, blob.getContent());
                } else {
                    System.out.println("File does not exist in that commit.");
                    return;
                }

            } else {
                File commitFile = new File(Repository.COMMITS + "/" + commitID + ".txt");
                Commit currCommit = Utils.readObject(commitFile, Commit.class);
                HashMap<String, String> linkedBlobs = currCommit.getLinkedBlob();
                if (linkedBlobs.containsKey(toCheckout)) {
                    if (f.exists()) {
                        f.delete();
                    }
                    File blobFile = new File(Repository.BLOBS + "/"
                            + currCommit.getLinkedBlob().get(toCheckout) + ".txt");
                    Blob blob = Utils.readObject(blobFile, Blob.class);
                    File newFile = new File(Repository.CWD + "/" + toCheckout);
                    Utils.writeContents(newFile, blob.getContent());
                } else {
                    System.out.println("File does not exist in that commit.");
                    return;
                }
            }
        } else {
            System.out.println("Incorrect operands.");
        }
    }


    public void checkout3(String branchName) {
        File branchFile = new File(Repository.REFS + "/" + branchName + ".txt");
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String headName = Utils.readContentsAsString(headFile);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(headName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String commitID = Utils.readContentsAsString(branchFile);
        File commitFile = new File(Repository.COMMITS + "/" + commitID + ".txt");
        Commit committoCheckout = Utils.readObject(commitFile, Commit.class);
        Commit currCommit = getCurrentCommit();
        ArrayList<String> committedFiles = new ArrayList(currCommit.getLinkedBlob().keySet());
        ArrayList<String> filestoCheckout =
                new ArrayList(committoCheckout.getLinkedBlob().keySet());
        ArrayList<String> workingFiles = new ArrayList(Utils.plainFilenamesIn(Repository.CWD));
        for (String fileName : workingFiles) {
            if (!committedFiles.contains(fileName) && filestoCheckout.contains(fileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String fileName : filestoCheckout) {
            File file = new File(Repository.CWD + "/" + fileName);
            File blobFile = new File(Repository.BLOBS + "/"
                    + committoCheckout.getLinkedBlob().get(fileName) + ".txt");
            Blob blob = Utils.readObject(blobFile, Blob.class);
            Utils.writeContents(file, blob.getContent());
        }
        for (String fileName : workingFiles) {
            if (!filestoCheckout.contains(fileName) && committedFiles.contains(fileName)) {
                File file = new File(Repository.CWD + "/" + fileName);
                file.delete();
            }
        }
        StagingArea addStage = getCurrentStage("addStage");
        StagingArea removeStage = getCurrentStage("removeStage");
        addStage.clearStage();
        removeStage.clearStage();
        Utils.writeContents(headFile, branchName);
    }

    public void beginningChecker(String branchName) {
        StagingArea addStage = getCurrentStage("addStage");
        StagingArea removeStage = getCurrentStage("removeStage");
        if (!addStage.getStagedFiles().isEmpty() || !removeStage.getStagedFiles().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File branchFile = new File(Repository.REFS + "/" + branchName + ".txt");
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String headBranchName = Utils.readContentsAsString(headFile);
        if (headBranchName.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    public void merge(String branchName) {
        StagingArea removeStage = getCurrentStage("removeStage");
        beginningChecker(branchName);
        Commit splitPoint = splitPointGetter(branchName);
        Commit givenBranch = getBranch(branchName);
        Commit head = getCurrentCommit();
        if (splitPoint.getID().equals(head.getID())) {
            File headBranch = new File(Repository.HEAD + "/HEAD.txt");
            String headName = Utils.readContentsAsString(headBranch);
            File headPointer = new File(Repository.REFS + "/" + headName + ".txt");
            Utils.writeContents(headPointer, givenBranch.getID());
            System.out.println("Current branch fast-forwarded.");
        }
        if (splitPoint.getID().equals(givenBranch.getID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        HashMap<String, String> splitPointFiles = splitPoint.getLinkedBlob();
        HashMap<String, String> givenBranchFiles = givenBranch.getLinkedBlob();
        HashMap<String, String> headBranchFiles = head.getLinkedBlob();
        ArrayList<String> workingFiles =
                new ArrayList<>(Utils.plainFilenamesIn(Repository.CWD));
        for (String file : workingFiles) {
            if (!headBranchFiles.containsKey(file) && givenBranchFiles.containsKey(file)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        for (String file : workingFiles) {
            if (splitPointFiles.containsKey(file) && givenBranchFiles.containsKey(file)
                    && headBranchFiles.containsKey(file)) {
                if (splitPointFiles.get(file).equals(headBranchFiles.get(file))
                        && !splitPointFiles.get(file).equals(givenBranchFiles.get(file))) {
                    checkout2(givenBranch.getID(), file, "--");
                    add(file);
                }
                if (!splitPointFiles.get(file).equals(headBranchFiles.get(file))
                        && !splitPointFiles.get(file).equals(givenBranchFiles.get(file))
                        && !headBranchFiles.get(file).equals(givenBranchFiles.get(file))) {
                    mergeHelper(file, branchName);
                }
            }
            if (!givenBranchFiles.containsKey(file) && splitPointFiles.containsKey(file)
                    && headBranchFiles.containsKey(file)) {
                if (!splitPointFiles.get(file).equals(headBranchFiles.get(file))) {
                    mergeHelper2(file, branchName);
                }
            }
            if (!headBranchFiles.containsKey(file) && splitPointFiles.containsKey(file)
                    && givenBranchFiles.containsKey(file)) {
                if (!splitPointFiles.get(file).equals(givenBranchFiles.get(file))) {
                    mergeHelper(file, branchName);
                }
            }
        }
        for (String file : splitPointFiles.keySet()) {
            if (headBranchFiles.containsKey(file) && !givenBranchFiles.containsKey(file)) {
                if (splitPointFiles.get(file).equals(headBranchFiles.get(file))) {
                    Commit currCommit = getCurrentCommit();
                    String blobHash = currCommit.getLinkedBlob().get(file);
                    removeStage.add(file, blobHash);
                    saveStage(removeStage, "removeStage");
                    File original = new File(Repository.CWD + "/" + file);
                    if (original.exists()) {
                        original.delete();
                    }
                }
            }
        }
        for (String file : givenBranchFiles.keySet()) {
            if (!headBranchFiles.containsKey(file) && !splitPointFiles.containsKey(file)) {
                checkout2(givenBranch.getID(), file, "--");
                add(file);
            }
        }
        conflictChecker(branchName);
    }

    public void mergeHelper(String file, String branchName) {
        Commit splitPoint = splitPointGetter(branchName);
        Commit givenBranch = getBranch(branchName);
        Commit head = getCurrentCommit();
        HashMap<String, String> splitPointFiles = splitPoint.getLinkedBlob();
        HashMap<String, String> givenBranchFiles = givenBranch.getLinkedBlob();
        HashMap<String, String> headBranchFiles = head.getLinkedBlob();
        File original = new File(Repository.CWD + "/" + file);
        String headBlobName = headBranchFiles.get(file);
        String givenBlobName = givenBranchFiles.get(file);
        File headBlobFile = new File(Repository.BLOBS + "/"
                + headBlobName + ".txt");
        File givenBlobFile = new File(Repository.BLOBS + "/"
                + givenBlobName + ".txt");
        Blob headBlob = Utils.readObject(headBlobFile, Blob.class);
        Blob givenBlob = Utils.readObject(givenBlobFile, Blob.class);
        Utils.writeContents(original, "<<<<<<< HEAD"
                + "\n" + headBlob.getContent()
                + "======="
                + "\n" + givenBlob.getContent()
                + ">>>>>>>"
                + "\n");
        ifConflict = true;
    }

    public void mergeHelper2(String file, String branchName) {
        Commit splitPoint = splitPointGetter(branchName);
        Commit givenBranch = getBranch(branchName);
        Commit head = getCurrentCommit();
        HashMap<String, String> splitPointFiles = splitPoint.getLinkedBlob();
        HashMap<String, String> givenBranchFiles = givenBranch.getLinkedBlob();
        HashMap<String, String> headBranchFiles = head.getLinkedBlob();
        File original = new File(Repository.CWD + "/" + file);
        String headBlobName = headBranchFiles.get(file);
        File headBlobFile = new File(Repository.BLOBS + "/"
                + headBlobName + ".txt");
        Blob headBlob = Utils.readObject(headBlobFile, Blob.class);
        Utils.writeContents(original, "<<<<<<< HEAD"
                + "\n" + headBlob.getContent()
                + "======="
                + "\n"
                + ">>>>>>>"
                + "\n");
        ifConflict = true;
    }

    public void conflictChecker(String branchName) {
        if (ifConflict) {
            System.out.println("Encountered a merge conflict.");
            return;
        } else {
            String merged =
                    Utils.readContentsAsString(new File(Repository.HEAD + "/HEAD.txt"));
            commit("Merged " + branchName + " into " + merged + ".");
        }

    }

    public Commit splitPointGetter(String branchName) {
        Commit currBranch = getCurrentCommit();
        Commit givenBranch = getBranch(branchName);
        Commit splitPoint = null;
        HashMap<String, Commit> currCommitTree = new HashMap<>();
        while (currBranch != null && currBranch.getParent() != null) {
            currCommitTree.put(currBranch.getID(), currBranch);
            currBranch = getParentCommit(currBranch);
            if (currBranch.getParent() == null) {
                currCommitTree.put(currBranch.getID(), currBranch);
                splitPoint = currBranch;
            }
        }
        while (givenBranch != null && givenBranch.getParent() != null) {
            if (currCommitTree.containsKey(givenBranch.getID())) {
                splitPoint = currCommitTree.get(givenBranch.getID());
                break;
            }
            givenBranch = getParentCommit(givenBranch);
        }
        return splitPoint;
    }

    public void reset(String commitID) {
        ArrayList<String> commitList = new ArrayList<>(Utils.plainFilenamesIn(Repository.COMMITS));
        if (!commitList.contains(commitID + ".txt")) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String headBranchName = Utils.readContentsAsString(headFile);
        File headBranchFile = new File(Repository.REFS + "/"
                + headBranchName + ".txt");
        String currentBranch = Utils.readContentsAsString(headBranchFile);
        File currCommitFile = new File(Repository.COMMITS + "/"
                + currentBranch + ".txt");
        Commit currCommit = Utils.readObject(currCommitFile, Commit.class);
        ArrayList<String> currentTrackedFiles =
                new ArrayList<>(currCommit.getLinkedBlob().keySet());
        File commitFile = new File(Repository.COMMITS + "/"
                + commitID + ".txt");
        Commit givenCommit = Utils.readObject(commitFile, Commit.class);
        ArrayList<String> filestoCheckout = new ArrayList<>(givenCommit.getLinkedBlob().keySet());
        ArrayList<String> workingFiles = new ArrayList<>(Utils.plainFilenamesIn(Repository.CWD));
        for (String workFile : workingFiles) {
            if (currentTrackedFiles.contains(workFile) && !filestoCheckout.contains(workFile)) {
                File origional = new File(Repository.CWD + "/" + workFile);
                origional.delete();
            }
        }

        for (String workingFile : workingFiles) {
            if (!currentTrackedFiles.contains(workingFile)
                    && filestoCheckout.contains(workingFile)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String file : filestoCheckout) {
            checkout2(commitID, file, "--");
        }
        Utils.writeContents(headBranchFile, commitID);
        StagingArea addStage = getCurrentStage("addStage");
        StagingArea removeStage = getCurrentStage("removeStage");
        addStage.clearStage();
        removeStage.clearStage();
        saveStage(addStage, "addStage");
        saveStage(removeStage, "removeStage");

    }

    public void rm(String fileName) {
        StagingArea currAddStage = getCurrentStage("addStage");
        Commit currCommit = getCurrentCommit();
        ArrayList<String> filesTrackedbyCommit = new ArrayList(currCommit.getLinkedBlob().keySet());
        ArrayList<String> filesToCommit = new ArrayList(currAddStage.getStagedFiles().keySet());
        if (filesToCommit.contains(fileName) || filesTrackedbyCommit.contains(fileName)) {
            if (filesToCommit.contains(fileName)) {
                currAddStage.remove(fileName);
                saveStage(currAddStage, "addStage");
            }
            if (filesTrackedbyCommit.contains(fileName)) {
                StagingArea removeStage = getCurrentStage("removeStage");
                String blobHash = currCommit.getLinkedBlob().get(fileName);
                removeStage.add(fileName, blobHash);
                saveStage(removeStage, "removeStage");
                File original = new File(Repository.CWD + "/" + fileName);
                if (original.exists()) {
                    original.delete();
                }
            }
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public void branch(String branchName) {
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String headFileName = Utils.readContentsAsString(headFile);
        File headBranchFile = new File(Repository.REFS + "/" + headFileName + ".txt");
        String commitID = Utils.readContentsAsString(headBranchFile);
        File newPointerFile = new File(Repository.REFS + "/" + branchName + ".txt");
        if (newPointerFile.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            Utils.writeContents(newPointerFile, commitID);
        }
    }

    public void status() {
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        if (!headFile.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            System.out.println("=== Branches ===");
            String headBranchName = Utils.readContentsAsString(headFile);
            ArrayList<String> brachNames = new ArrayList(Utils.plainFilenamesIn(Repository.REFS));
            ArrayList<String> outputNames = new ArrayList<>();
            outputNames.add("*" + headBranchName);
            for (String branch : brachNames) {
                String output = branch.substring(0, branch.length() - 4);
                if (!output.equals(headBranchName)) {
                    outputNames.add(output);
                }
            }
            Collections.sort(outputNames);
            for (String output : outputNames) {
                System.out.println(output);
            }
            System.out.println();
            System.out.println("=== Staged Files ===");
            StagingArea addStage = getCurrentStage("addStage");
            ArrayList<String> stagedFiles = new ArrayList<>(addStage.getStagedFiles().keySet());
            ArrayList<String> addStageNames = new ArrayList<>();
            for (String stagedFile : stagedFiles) {
                addStageNames.add(stagedFile);
            }
            Collections.sort(addStageNames);
            for (String name : addStageNames) {
                System.out.println(name);
            }
            System.out.println();
            System.out.println("=== Removed Files ===");
            StagingArea removeStage = getCurrentStage("removeStage");
            ArrayList<String> removeStageNames = new ArrayList<>();
            ArrayList<String> removeddFiles =
                    new ArrayList<>(removeStage.getStagedFiles().keySet());
            for (String removedFile : removeddFiles) {
                removeStageNames.add(removedFile);
            }
            Collections.sort(removeStageNames);
            for (String name : removeStageNames) {
                System.out.println(name);
            }
            System.out.println();
            System.out.println("=== Modifications Not Staged For Commit ===");
            System.out.println();
            System.out.println("=== Untracked Files ===");
        }
    }

    public void rmb(String branchName) {
        File branchFile = new File(Repository.REFS + "/" + branchName + ".txt");
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String headBranchName = Utils.readContentsAsString(headFile);
        if (branchName.equals(headBranchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branchFile.delete();
    }

    public Commit getCurrentCommit() {
        File headFile = new File(Repository.HEAD + "/HEAD.txt");
        String pointer = Utils.readContentsAsString(headFile);
        File pointerFile = new File(Repository.REFS + "/" + pointer + ".txt");
        String commitID = Utils.readContentsAsString(pointerFile);
        File commitFile = new File(Repository.COMMITS + "/" + commitID + ".txt");
        Commit currCommit = Utils.readObject(commitFile, Commit.class);
        return currCommit;
    }

    public StagingArea getCurrentStage(String stageType) {
        File stageFile = new File(Repository.STAGE + "/" + stageType + ".txt");
        StagingArea stage = Utils.readObject(stageFile, StagingArea.class);
        return stage;
    }

    public void saveCommit(Commit commit) {
        File commitFile = new File(Repository.COMMITS + "/" + commit.getID() + ".txt");
        Utils.writeObject(commitFile, commit);
    }

    public void saveStage(StagingArea stage, String stageType) {
        File stageFile = new File(Repository.STAGE + "/" + stageType + ".txt");
        Utils.writeObject(stageFile, stage);
    }

    public void saveBlob(Blob blob) {
        String blobHash = Utils.sha1(blob.getContent());
        blob.setID(blobHash);
        File blobFile = new File(Repository.BLOBS + "/" + blobHash + ".txt");
        Utils.writeObject(blobFile, blob);
    }

    public void saveBranch(String uid, String brachName) {
        File headFile = new File(Repository.REFS + "/" + brachName + ".txt");
        Utils.writeContents(headFile, uid);
    }

    public Commit getParentCommit(Commit commit) {
        String parent = commit.getParent();
        File parentCommitFile = new File(Repository.COMMITS + "/" + parent + ".txt");
        Commit prCommit = Utils.readObject(parentCommitFile, Commit.class);
        return prCommit;
    }

    public Commit getBranch(String branchName) {
        File branchFile = new File(Repository.REFS + "/" + branchName + ".txt");
        String commitName = Utils.readContentsAsString(branchFile);
        File commitFile = new File(Repository.COMMITS + "/" + commitName + ".txt");
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }
}

