package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static su.litvak.justdlna.util.HashHelper.sha1;

public class FolderNode<T extends Enum<T> & MediaFormat> extends ContainerNode {
    final File folder;
    final Class<T> formatClass;

    @JsonCreator
    public FolderNode(@JsonProperty("title")
                      String title,
                      @JsonProperty("path")
                      File folder,
                      @JsonProperty("format")
                      Class<T> formatClass) {
        super(contentId(formatClass, folder), title == null || title.trim().isEmpty() ? folder.getName() : title);
        this.folder = folder;
        this.formatClass = formatClass;
    }

    public FolderNode(File folder, Class<T> formatClass) {
        super(contentId(formatClass, folder), folder.getName());
        this.folder = folder;
        this.formatClass = formatClass;
    }

    @Override
    public List<ContainerNode> getContainers() {
        List<ContainerNode> result = new ArrayList<ContainerNode>(super.getContainers());
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                FolderNode subFolder = new FolderNode(file, formatClass);
                if (!subFolder.getItems().isEmpty() || !subFolder.getContainers().isEmpty()) {
                    result.add(subFolder);
                    subFolder.setParent(this);
                }
            }
        }
        return result;
    }

    @Override
    public List<ItemNode> getItems() {
        List<ItemNode> result = new ArrayList<ItemNode>();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                MediaFormat format = getFormat(file.getName().substring(file.getName().lastIndexOf('.') + 1).toUpperCase(), this.formatClass);
                if (format == null) {
                    continue;
                }
                ItemNode itemNode = new ItemNode(contentId(this.formatClass, file), file, format);
                result.add(itemNode);
                itemNode.setParent(this);
            }
        }
        return result;
    }

    public File getFolder() {
        return folder;
    }

    public Class<T> getFormatClass() {
        return formatClass;
    }

    private static String contentId(Class<? extends MediaFormat> formatClass, File folder) {
        return formatClass.getName() + (sha1(folder.getAbsolutePath()) + "-" + getSafeName(folder));
    }

    private static String getSafeName (final File folder) {
        return folder.getName().replaceAll("[^a-zA-Z0-9]", "_");
    }

    public static <T extends Enum<T> & MediaFormat> T getFormat(final String value, final Class<T> enumClass) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException iae) {
        }
        return null;
    }
}
