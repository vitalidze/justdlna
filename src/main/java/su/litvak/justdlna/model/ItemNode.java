package su.litvak.justdlna.model;

import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.item.Item;
import org.teleal.common.util.MimeType;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.http.MediaStreamHandler;

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
        String url = "http://" + Config.get().getIpAddress() + ":" + Config.get().getHttpPort();
        final Res res = new Res(extMimeType, Long.valueOf(file.length()), url + MediaStreamHandler.PREFIX + getId());
        res.setSize(file.length());
        Item item = format.createItem(getId(), file.getName(), res);
        item.setParentID(getParent().getId());
        return item;
    }

    public File getFile() {
        return file;
    }

    public MediaFormat getFormat() {
        return format;
    }
}
