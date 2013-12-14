package su.litvak.justdlna.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import su.litvak.justdlna.provider.MediaFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static su.litvak.justdlna.util.HashHelper.sha1;

public class FolderNode extends ContainerNode {
    final File folder;
    final String title;
    final Class<? extends MediaFormat> format;

    @JsonCreator
    public FolderNode(@JsonProperty("title")
                      String title,
                      @JsonProperty("path")
                      File folder,
                      @JsonProperty("format")
                      Class<? extends MediaFormat> format) {
        super(null, contentId(format, folder));
        this.folder = folder;
        this.title = title;
        this.format = format;
    }

    public FolderNode(ContainerNode parent, File folder, Class<? extends MediaFormat> format) {
        super(parent, contentId(format, folder));
        this.folder = folder;
        this.title = folder.getName();
        this.format = format;
    }

    @Override
    Container createContainer() {
        final Container container = new Container();
        container.setClazz(new DIDLObject.Class("object.container"));
        container.setId(id);
        container.setTitle(title);
        container.setRestricted(true);
        container.setWriteStatus(WriteStatus.NOT_WRITABLE);
        container.setChildCount(Integer.valueOf(0));
        return parent.addChild(container);
    }

    @Override
    public List<ContainerNode> getContainers() {
        List<ContainerNode> result = new ArrayList<ContainerNode>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                FolderNode subFolder = new FolderNode(this, file, format);
                if (!subFolder.getItems().isEmpty()) {
                    result.add(subFolder);
                }
            }
        }
        return result;
    }

    @Override
    public List<ItemNode> getItems() {
        // TODO
        return Collections.emptyList();
    }

    public File getFolder() {
        return folder;
    }

    private static String contentId(Class<? extends MediaFormat> format, File folder) {
        return format.getName() + (sha1(folder.getAbsolutePath()) + "-" + getSafeName(folder));
    }

    private static String getSafeName (final File folder) {
        return folder.getName().replaceAll("[^a-zA-Z0-9]", "_");
    }
}
