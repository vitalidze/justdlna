package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualFolderNode extends ContainerNode {
    private List<ContainerNode> containers = Collections.emptyList();

    protected VirtualFolderNode() {
        super(null, null);
    }

    public VirtualFolderNode(@JsonProperty("title")
                             String title) {
        super("Virtual-Folder-" + Integer.toString(idGenerator.getAndIncrement()), title);
    }

    public VirtualFolderNode(List<ContainerNode> containers) {
        this("<NO TITLE>");
        setContainers(containers);
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

    @Override
    public List<ItemNode> getItems() {
        return Collections.emptyList();
    }

    public ContainerNode addContainer(ContainerNode container) {
        if (!(containers instanceof ArrayList<?>)) {
            containers = new ArrayList<ContainerNode>(containers);
        }
        containers.add(container);
        container.setParent(this);
        return this;
    }
}
