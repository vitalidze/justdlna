package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import su.litvak.justdlna.Config;

import java.util.*;

public class LastAddedNode<T extends Enum<T> & MediaFormat> extends ContainerNode {
    final Class<T> formatClass;
    final int limit;

    @JsonCreator
    public LastAddedNode(@JsonProperty("title") String title,
                         @JsonProperty("format") Class<T> formatClass,
                         @JsonProperty("limit") Integer limit) {
        super("Last-Added-" + idGenerator.getAndIncrement(),
              title == null || title.trim().isEmpty() ? "Last added " + formatClass.getSimpleName().substring(0, formatClass.getSimpleName().indexOf("Format")) : title);
        this.formatClass = formatClass;
        this.limit = limit == null ? 10 : limit.intValue();
    }

    @Override
    public List<ContainerNode> getContainers() {
        return Collections.emptyList();
    }

    @Override
    public List<ItemNode> getItems() {
        Set<ItemNode> allItems = new HashSet<ItemNode>();
        allItems.addAll(getAllItems(Config.get().getContent()));
        List<ItemNode> sortedItems = new ArrayList<ItemNode>(allItems);
        Collections.sort(sortedItems, new Comparator<ItemNode>() {
            @Override
            public int compare(ItemNode o1, ItemNode o2) {
                return Long.valueOf(o2.file.lastModified()).compareTo(Long.valueOf(o1.file.lastModified()));
            }
        });
        return sortedItems.subList(0, Math.min(limit, sortedItems.size()));
    }

    private Set<ItemNode> getAllItems(ContainerNode folderNode) {
        Set<ItemNode> result = new HashSet<ItemNode>();
        if (folderNode instanceof FolderNode<?> &&
            ((FolderNode<?>) folderNode).formatClass == formatClass) {
            result.addAll(folderNode.getItems());
        }
        for (ContainerNode containerNode : folderNode.getContainers()) {
            result.addAll(getAllItems(containerNode));
        }
        return result;
    }
}
