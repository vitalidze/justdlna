package su.litvak.justdlna.model;

import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import su.litvak.justdlna.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RootNode extends ContainerNode {
    private List<? extends ContainerNode> topLevelContainers;

    public RootNode(List<? extends ContainerNode> topLevelContainers) {
        super("0");
        this.topLevelContainers = topLevelContainers;
        for (ContainerNode containerNode : topLevelContainers) {
            containerNode.setParent(this);
        }
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
    public List<? extends ContainerNode> getContainers() {
        return topLevelContainers;
    }

    @Override
    public List<ItemNode> getItems() {
        return Collections.emptyList();
    }
}
