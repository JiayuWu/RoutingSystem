package project1;

import java.io.Serializable;

public class Link implements Serializable {
    String linkID;
    String linkName;
    String fromNodeID;
    String toNodeID;
    Position[] pos;

    public Link() {
    }
}