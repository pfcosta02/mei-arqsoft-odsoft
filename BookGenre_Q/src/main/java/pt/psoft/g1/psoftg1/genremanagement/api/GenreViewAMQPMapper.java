package pt.psoft.g1.psoftg1.genremanagement.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class GenreViewAMQPMapper extends MapperInterface {

    @Mapping(target = "pk", source = "pk")
    @Mapping(target = "genre", source = "genre")

    public abstract GenreViewAMQP toGeViewAMQP(Genre genre);

    public abstract List<GenreViewAMQP> toGenreViewAMQP(List<Genre> genreList);
}
