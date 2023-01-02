package gitlet;

import java.io.File;

import static gitlet.Utils.join;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File STAGE = join(GITLET_DIR, "stage");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    public static final File COMMITS = join(GITLET_DIR, "commits");

    public static final File REFS = join(GITLET_DIR, "refs");
    public static final File HEAD = join(GITLET_DIR, "HEAD");


    public static void setupPersistence() {
        GITLET_DIR.mkdir();
        STAGE.mkdir();
        BLOBS.mkdir();
        COMMITS.mkdir();
        REFS.mkdir();
        HEAD.mkdir();
    }

    public static Boolean checkExistence() {
        return !GITLET_DIR.exists();
    }
}
