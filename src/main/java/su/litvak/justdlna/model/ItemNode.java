package su.litvak.justdlna.model;

import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.item.Item;
import org.teleal.common.util.MimeType;

import java.io.File;

public class ItemNode extends ContentNode {
    final File file;
    final MediaFormat format;

    ItemNode(String id, File file, MediaFormat format) {
        super(id);

        this.file = file;
        this.format = format;
    }

    public Item getItem() {
        final String mime = format.getMime();
        final MimeType extMimeType = new MimeType(mime.substring(0, mime.indexOf('/')), mime.substring(mime.indexOf('/') + 1));
        // TODO
        String url = "http://abc:3333";
        final Res res = new Res(extMimeType, Long.valueOf(file.length()), url + "/" + id);
        res.setSize(file.length());
        Item item = format.createItem(id, file.getName(), res);
        item.setParentID(getParent().getId());
        return item;
    }
}
