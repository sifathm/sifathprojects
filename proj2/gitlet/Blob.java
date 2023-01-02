package gitlet;

import java.io.Serializable;

public class Blob implements Serializable {
    private final String content;
    private String id;

    public Blob(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String number) {
        this.id = number;
    }


}
