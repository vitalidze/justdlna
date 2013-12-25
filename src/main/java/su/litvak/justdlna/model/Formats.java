package su.litvak.justdlna.model;

public enum Formats {
    VIDEO(VideoFormat.class),
    AUDIO(AudioFormat.class),
    IMAGE(ImageFormat.class);

    private final Class<? extends MediaFormat> formatClass;

    private Formats(Class<? extends MediaFormat> formatClass) {
        this.formatClass = formatClass;
    }

    public static <T extends Enum<T> & MediaFormat> Class<T> fromString(String s) {
        return (Class<T>) valueOf(s.toUpperCase()).formatClass;
    }

    public static String toString(Class<? extends MediaFormat> formatClass) {
        for (Formats f : values()) {
            if (f.formatClass == formatClass) {
                return f.name().toLowerCase();
            }
        }
        throw new IllegalArgumentException("Unsupported class: " + formatClass.getName());
    }
}
