package by.klihal.waittor.model;

public enum TorrentType {

    OUR("312", "Наше кино"),
    FOREIGN("313", "Зарубежное кино"),
    SERIES("1803", "Сериал");

    private final String value;
    private final String name;

    TorrentType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
