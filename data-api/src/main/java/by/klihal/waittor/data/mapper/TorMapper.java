package by.klihal.waittor.data.mapper;

import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.data.model.Torrent;

import by.klihal.waittor.common.enums.TorrentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TorMapper {

    @Mapping(source = "torDto", target = "torrentType", qualifiedByName = "fromTorrentEnum")
    Torrent toEntity(TorDto torDto);

    @Mapping(source = "torrent", target = "torrentType", qualifiedByName = "toTorrentEnum")
    TorDto toDto(Torrent torrent);

    @Named("fromTorrentEnum")
    default String fromTorrentEnum(TorDto dto) {
        return dto.torrentType().toString();
    }

    @Named("toTorrentEnum")
    default TorrentType toTorrentEnum(Torrent entity) {
        return TorrentType.valueOf(entity.getTorrentType());
    }
}
