package su.litvak.minidlna.provider;

public enum ImageFormat implements MediaFormat {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png");

    private final String ext;
    private final String mime;

    private ImageFormat(final String ext, final String mime) {
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
