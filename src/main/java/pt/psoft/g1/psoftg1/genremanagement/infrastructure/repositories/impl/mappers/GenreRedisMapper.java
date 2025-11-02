package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.redis.GenreRedisDTO;

@Mapper(componentModel = "spring")
public interface GenreRedisMapper {

    @Mappings({
            @Mapping(target = "pk", source = "pk"),
            @Mapping(target = "genre", source = "genre")
    })
    Genre toDomain(GenreRedisDTO dto);

    @Mappings({
            @Mapping(target = "pk", source = "pk"),
            @Mapping(target = "genre", source = "genre")
    })
    GenreRedisDTO toDTO(Genre genre);
}
