package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers;


import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch.GenreES;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreESMapper {

    // Genre (JPA) -> GenreES (Elasticsearch)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genre", source = "genre")
    GenreES toEntity(Genre genre);

    // GenreES (Elasticsearch) -> Genre (JPA)
    Genre toModel(GenreES genreES);
}