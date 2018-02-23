package su.litvak.justdlna.model;

public abstract class ContentNode implements Comparable<ContentNode> {
    private String id;
    private ContainerNode parent;

    ContentNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public final void setParent(ContainerNode parent) {
        this.parent = parent;
    }

    public ContainerNode getParent() {
        return parent;
    }

    public abstract String getTitle();

    @Override
    public int compareTo(ContentNode that) {
        String thisTitle = this.getTitle() == null ? "" : this.getTitle();
        String thatTitle = that.getTitle() == null ? "" : that.getTitle();
        return thisTitle.compareTo(thatTitle);
    }
}
