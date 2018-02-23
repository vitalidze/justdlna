package su.litvak.justdlna.model;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

public interface MediaFormat {
    String getMime();
    String getExt();

    Item createItem(String id, String title, Res res);
}
