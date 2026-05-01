package by.klihal.waittor.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Torrent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TorrentType torrentType;
    private LocalDate release;
    private Integer series;

    public Torrent() {
    }

    public Torrent(Long id, String name, TorrentType torrentType, LocalDate release, Integer series) {
        this.id = id;
        this.name = name;
        this.torrentType = torrentType;
        this.release = release;
        this.series = series;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TorrentType getTorrentType() {
        return torrentType;
    }

    public void setTorrentType(TorrentType torrentType) {
        this.torrentType = torrentType;
    }

    public LocalDate getRelease() {
        return release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }
}
