package su.litvak.justdlna.model;

import org.teleal.cling.support.model.container.Container;

import java.util.List;

public abstract class ContainerNode extends ContentNode {
    public ContainerNode(String id) {
        super(id);
    }

    public final Container getContainer() {
        Container container = createContainer();
        container.setChildCount(getChildCount());
        if (getParent() != null) {
            container.setParentID(getParent().getId());
        }
        return container;
    }

    abstract Container createContainer();
    public abstract List<? extends ContainerNode> getContainers();
    public abstract List<ItemNode> getItems();

    public final int getChildCount() {
        return getContainers().size() + getItems().size();
    }
}
