package su.litvak.justdlna.provider;

import su.litvak.justdlna.model.ContentNode;

import java.util.List;

public interface ContentProvider {
    public ContentNode getRoot();
    public List<ContentNode> getChildren(ContentNode parent);
}
