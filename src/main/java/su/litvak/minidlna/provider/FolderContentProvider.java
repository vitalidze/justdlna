package su.litvak.minidlna.provider;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import su.litvak.minidlna.ContentNode;
import su.litvak.minidlna.FolderNode;
import su.litvak.minidlna.RootNode;

import java.io.File;
import java.util.List;

import static su.litvak.minidlna.util.HashHelper.sha1;

public class FolderContentProvider implements ContentProvider {
    final FolderNode folder;
    final String title;
    final ImageFormat format;

    @JsonCreator
    public FolderContentProvider(@JsonProperty("title") String title,
                                 @JsonProperty("folder") File folder,
                                 @JsonProperty("format") ImageFormat format) {
        this.title = title;
        this.folder = new FolderNode(RootNode.get(), contentId(format, folder), title, folder);
        this.format = format;
    }

    @Override
    public ContentNode getRoot() {
        return folder;
    }

    @Override
    public List<ContentNode> getChildren(ContentNode parent) {
        return null;
    }

    private static String contentId(MediaFormat format, File folder) {
        return format.getClass().getName() + (sha1(folder.getAbsolutePath()) + "-" + getSafeName(folder));
    }

    private static String getSafeName (final File folder) {
        return folder.getName().replaceAll("[^a-zA-Z0-9]", "_");
    }
}