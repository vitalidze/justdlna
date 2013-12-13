package su.litvak.minidlna.provider;

import su.litvak.minidlna.ContentNode;

import java.util.List;

public interface ContentProvider {
    public ContentNode getRoot();
    public List<ContentNode> getChildren(ContentNode parent);
}
