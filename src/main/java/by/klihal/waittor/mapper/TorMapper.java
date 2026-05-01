package by.klihal.waittor.mapper;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.model.Torrent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TorMapper {

    Torrent toEntity(TorDto torDto);

    List<TorDto> toDtoList(List<Torrent> torrent);
}
