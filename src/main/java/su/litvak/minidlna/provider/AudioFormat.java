package su.litvak.minidlna.provider;

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
}
