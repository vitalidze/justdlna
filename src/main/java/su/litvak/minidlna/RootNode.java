package su.litvak.minidlna;

import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;

public class RootNode extends ContainerNode {
    private final static RootNode ROOT_NODE = new RootNode();

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

    public static RootNode get() {
        return ROOT_NODE;
    }
}
