package by.klihal.waittor.model;

public enum TorrentType {

    OUR("312"),
    FOREIGN("313"),
    SERIES("1803");

    private final String value;

    TorrentType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
