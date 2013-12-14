package su.litvak.justdlna.model;

import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.provider.ContentProvider;
import su.litvak.justdlna.provider.FolderContentProvider;

import java.util.ArrayList;
import java.util.List;

public class RootNode extends ContainerNode implements ContentProvider {
    private final static RootNode ROOT_NODE = new RootNode();

    private List<ContentNode> children;

    private RootNode() {
        super(null, "0");
    }

    @Override
    Container createContainer() {
        final Container root = new Container();
        root.setId(id);
        root.setParentID("-1");
        root.setTitle("Root");
        root.setCreator(Config.METADATA_MODEL_NAME);
        root.setRestricted(true);
        root.setSearchable(true);
        root.setWriteStatus(WriteStatus.NOT_WRITABLE);
        root.setChildCount(Integer.valueOf(0));
        return root;
    }

    @Override
    public ContentProvider getContentProvider() {
        return this;
    }

    public static RootNode get() {
        return ROOT_NODE;
    }

    @Override
    public ContentNode getRoot() {
        return this;
    }

    @Override
    public List<ContentNode> getChildren(ContentNode parent) {
        if (children == null) {
            children = new ArrayList<ContentNode>(Config.get().getFolders().size());
            for (FolderContentProvider folderContentProvider : Config.get().getFolders()) {
                children.add(folderContentProvider.getRoot());
                addChild(((FolderNode) folderContentProvider.getRoot()).getContainer());
            }
        }
        return children;
    }
}
