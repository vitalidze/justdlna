package su.litvak.justdlna.provider;

import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.item.Item;

public interface MediaFormat {
    String getMime();
    String getExt();

    Item createItem(String id, String title, Res res);
}
