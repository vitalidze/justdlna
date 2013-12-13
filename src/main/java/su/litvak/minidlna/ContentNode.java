package su.litvak.minidlna;

public abstract class ContentNode {
    final String id;
    final ContainerNode parent;

    ContentNode(ContainerNode parent, String id) {
        this.parent = parent;
        this.id = id;
    }
}
