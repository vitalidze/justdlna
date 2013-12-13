package su.litvak.minidlna.model;

import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

public abstract class ContainerNode extends ContentNode {
    private Container container;

    public ContainerNode(ContainerNode parent, String id) {
        super(parent, id);
        NodeMap.get().put(id, this);
    }

    public final Container getContainer() {
        if (container == null) {
            container = createContainer();
        }
        return container;
    }

    abstract Container createContainer();

    final <T extends DIDLObject> T addChild(T node) {
        if (node instanceof Item) {
            getContainer().addItem((Item) node);
        } else if (node instanceof Container) {
            getContainer().addContainer((Container) node);
        } else {
            throw new IllegalArgumentException("Unknown child type " + node.getClass());
        }

        node.setParentID(id);
        getContainer().setChildCount(Integer.valueOf(getContainer().getChildCount().intValue() + 1));

        return node;
    }
}
