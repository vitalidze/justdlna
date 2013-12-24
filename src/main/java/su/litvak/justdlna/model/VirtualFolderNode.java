package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collections;
import java.util.List;

public class VirtualFolderNode extends ContainerNode {
    public VirtualFolderNode(@JsonProperty("title")
                             String title) {
        super("Virtual-Folder-" + Integer.toString(idGenerator.getAndIncrement()), title);
    }

    public VirtualFolderNode(List<ContainerNode> containers) {
        this("<NO TITLE>");
        setContainers(containers);
    }

    @Override
    public List<ItemNode> getItems() {
        return Collections.emptyList();
    }
}
