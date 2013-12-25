package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import su.litvak.justdlna.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type (name="folder", value=FolderNode.class),
               @JsonSubTypes.Type (name="last-added", value=LastAddedNode.class),
               @JsonSubTypes.Type (name="virtual", value=VirtualFolderNode.class)})
public abstract class ContainerNode extends ContentNode {
    static AtomicInteger idGenerator = new AtomicInteger(0);
    final String title;
    List<ContainerNode> containers = Collections.emptyList();

    public ContainerNode(String id, String title) {
        super(id);
        this.title = title;
    }

    public final Container getContainer() {
        Container container = createContainer();
        container.setChildCount(getChildCount());
        return container;
    }

    final Container createContainer() {
        Container container = new Container();
        container.setId(getId());
        container.setTitle(title);
        container.setParentID(getParent() == null ? "-1" : getParent().getId());
        container.setClazz(new DIDLObject.Class("object.container"));
        if (getParent() == null) {
            container.setCreator(Config.METADATA_MODEL_NAME);
        }
        container.setRestricted(true);
        container.setSearchable(true);
        container.setWriteStatus(WriteStatus.NOT_WRITABLE);
        container.setChildCount(Integer.valueOf(0));
        return container;
    }
    public List<ContainerNode> getContainers() {
        return containers;
    }

    @JsonProperty("folders")
    public void setContainers(List<ContainerNode> containers) {
        this.containers = containers;
        for (ContainerNode container : containers) {
            container.setParent(this);
        }
    }
    public abstract List<ItemNode> getItems();

    public final int getChildCount() {
        return getContainers().size() + getItems().size();
    }

    public ContainerNode addContainer(ContainerNode container) {
        if (!(containers instanceof ArrayList<?>)) {
            containers = new ArrayList<ContainerNode>(containers);
        }
        containers.add(container);
        container.setParent(this);
        return this;
    }

    public <T extends Enum<T> & MediaFormat> Class<T> getFormatClass() {
        return null;
    }
}
