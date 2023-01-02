package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class StagingArea implements Serializable {

    private HashMap<String, String> stagedFiles;

    public StagingArea() {
        stagedFiles = new HashMap<>();
    }

    public void add(String fileName, String sha1) {
        stagedFiles.put(fileName, sha1);
    }

    public void clearStage() {
        stagedFiles = new HashMap<>();
    }

    public void remove(String fileName) {
        stagedFiles.remove(fileName);
    }

    public HashMap<String, String> getStagedFiles() {
        return this.stagedFiles;
    }

}
