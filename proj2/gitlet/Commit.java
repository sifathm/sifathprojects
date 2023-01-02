package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */


    /**
     * The message of this Commit.
     */
    private final String message;
    private final String timestamp;
    private final String parent;
    private final HashMap<String, String> linkedBlob;
    private String id;


    public Commit(String message, String parent, HashMap<String, String> blobs) {
        this.message = message;
        this.parent = parent;
        linkedBlob = blobs;
        if (this.parent == null) {
            this.timestamp = "Thu Jan 1 00:00:00 1970 +0800";
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.US);
            this.timestamp = dateFormat.format(calendar.getTime());
        }
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getParent() {
        return this.parent;
    }

    public String getID() {
        return this.id;
    }

    public void setID(Commit commit) {
        byte[] neo = Utils.serialize(commit);
        id = Utils.sha1(neo);
    }

    public HashMap<String, String> getLinkedBlob() {
        return linkedBlob;
    }

}
