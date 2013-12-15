package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.teleal.cling.support.model.container.Container;

import java.util.List;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type (name="folder", value=FolderNode.class),
               @JsonSubTypes.Type (name="last-added", value=LastAddedNode.class)})
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
