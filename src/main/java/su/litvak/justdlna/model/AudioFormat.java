package su.litvak.justdlna.model;

import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.item.AudioItem;
import org.teleal.cling.support.model.item.Item;

public enum AudioFormat implements MediaFormat {
    MP3("mp3", "audio/mpeg"),
    OGG("ogg", "audio/ogg");

    private final String ext;
    private final String mime;

    private AudioFormat(final String ext, final String mime) {
        this.ext = ext;
        this.mime = mime;
    }

    @Override
    public String getMime () {
        return this.mime;
    }

    @Override
    public String getExt() {
        return ext;
    }

    @Override
    public Item createItem(String id, String title, Res res) {
        return new AudioItem(id, "", title, "", res);
    }
}
