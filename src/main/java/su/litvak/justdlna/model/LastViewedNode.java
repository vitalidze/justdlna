package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonProperty;
import su.litvak.justdlna.util.FileHelper;

import java.util.*;

public class LastViewedNode<T extends Enum<T> & MediaFormat> extends ContainerNode {
    final Class<T> formatClass;
    final int limit;

    public LastViewedNode(@JsonProperty("title") String title,
                          @JsonProperty("format") String format,
                          @JsonProperty("limit") Integer limit) {
        super("Last-Viewed-" + idGenerator.getAndIncrement(),
              title == null || title.trim().isEmpty() ? "Last viewed " + format : title);
        this.formatClass = Formats.fromString(format);
        this.limit = limit == null ? 10 : limit.intValue();
    }

    @Override
    public List<ContainerNode> getContainers() {
        return Collections.emptyList();
    }

    @Override
    public List<ItemNode> getItems() {
        Set<ItemNode> allItems = new HashSet<ItemNode>();
        allItems.addAll(getAllItems(getParent()));
        List<ItemNode> sortedItems = new ArrayList<ItemNode>(allItems);
        Collections.sort(sortedItems, new Comparator<ItemNode>() {
            @Override
            public int compare(ItemNode o1, ItemNode o2) {
                return Long.valueOf(FileHelper.getLastAccessTime(o2.file)).compareTo(Long.valueOf(FileHelper.getLastAccessTime(o1.file)));
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

    public Class<? extends MediaFormat> getFormatClass() {
        return formatClass;
    }
}
