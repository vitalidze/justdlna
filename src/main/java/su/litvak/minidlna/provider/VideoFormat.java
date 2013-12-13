package su.litvak.minidlna.provider;

public enum VideoFormat implements MediaFormat {

    AVI("avi", "video/avi"),
    MP4("mp4", "video/mp4"),
    M4V("m4v", "video/mp4"),
    MKV("mkv", "video/x-matroska"),
    FLV("flv", "video/x-flv"),
    WMV("wmv", "video/x-ms-wmv"),
    MPG("mpg", "video/mpeg"),
    MPEG("mpeg", "video/mpeg");

    private final String ext;
    private final String mime;

    private VideoFormat(final String ext, final String mime) {
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