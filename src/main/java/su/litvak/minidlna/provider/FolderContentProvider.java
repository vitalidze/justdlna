package su.litvak.minidlna.provider;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import su.litvak.minidlna.model.ContentNode;
import su.litvak.minidlna.model.FolderNode;
import su.litvak.minidlna.model.RootNode;

import java.io.File;
import java.util.List;

import static su.litvak.minidlna.util.HashHelper.sha1;

public class FolderContentProvider implements ContentProvider {
    @JsonIgnore
    final FolderNode folder;
    final String title;
    final Class<? extends MediaFormat> format;

    @JsonCreator
    public FolderContentProvider(@JsonProperty("title") String title,
                                 @JsonProperty("path") File folder,
                                 @JsonProperty("format") Class<? extends MediaFormat> format) {
        this.title = title;
        this.folder = new FolderNode(RootNode.get(), contentId(format, folder), title, folder);
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends MediaFormat> getFormat() {
        return format;
    }

    @JsonIgnore
    @Override
    public ContentNode getRoot() {
        return folder;
    }

    @JsonIgnore
    @Override
    public List<ContentNode> getChildren(ContentNode parent) {
        return null;
    }

    private static String contentId(Class<? extends MediaFormat> format, File folder) {
        return format.getName() + (sha1(folder.getAbsolutePath()) + "-" + getSafeName(folder));
    }

    private static String getSafeName (final File folder) {
        return folder.getName().replaceAll("[^a-zA-Z0-9]", "_");
    }
}