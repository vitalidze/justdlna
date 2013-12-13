package su.litvak.minidlna.model;

import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;

import java.io.File;

public class FolderNode extends ContainerNode {
    final File folder;
    final String title;

    public FolderNode(ContainerNode parent, String id, String title, File folder) {
        super(parent, id);
        this.folder = folder;
        this.title = title;
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

    public File getFolder() {
        return folder;
    }
}
