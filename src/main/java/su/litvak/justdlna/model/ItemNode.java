package su.litvak.justdlna.model;

import org.teleal.cling.support.model.item.Item;

public class ItemNode extends ContentNode {

    ItemNode(ContainerNode parent, String id) {
        super(parent, id);
    }

    public Item getItem() {
        return null;
    }
}
