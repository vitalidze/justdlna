package su.litvak.justdlna.chromecast;

import java.util.ArrayList;

public class Chromecasts extends ArrayList<DialServer> {
    private final static Chromecasts INSTANCE = new Chromecasts();

    private Chromecasts() {
    }

    public static Chromecasts get() {
        return INSTANCE;
    }

    public static void put(TrackedDialServers devices) {
        get().clear();
        for (DialServer device : devices) {
            get().add(device);
        }
    }
}
