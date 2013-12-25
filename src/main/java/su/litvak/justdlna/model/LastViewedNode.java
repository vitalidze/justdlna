package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.File;
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
        return ViewLog.getLastViewItems(limit, formatClass, getParent());
    }

    public <T extends Enum<T> & MediaFormat> Class<T> getFormatClass() {
        return (Class<T>) formatClass;
    }
}
