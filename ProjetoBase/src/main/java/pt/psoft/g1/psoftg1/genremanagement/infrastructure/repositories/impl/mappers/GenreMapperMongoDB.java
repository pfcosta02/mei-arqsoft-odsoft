package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;

@Mapper(componentModel = "spring")
public interface GenreMapperMongoDB {

    GenreMongoDB toMongoDB(Genre genre);
    Genre toModel(GenreMongoDB genreMongoDB);
}
