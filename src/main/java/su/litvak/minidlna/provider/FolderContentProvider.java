package su.litvak.minidlna.provider;

import su.litvak.minidlna.ContentNode;

import java.io.File;
import java.util.List;

public abstract class FolderContentProvider implements ContentProvider {
    File folder;
    ImageFormat format;

    @Override
    public ContentNode getRoot() {
        return null;
    }

    @Override
    public List<ContentNode> getChildren(ContentNode parent) {
        return null;
    }
}