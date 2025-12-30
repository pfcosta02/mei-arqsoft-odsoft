package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:59:33+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class GenreEntityMapperImpl implements GenreEntityMapper {

    @Override
    public Genre toModel(GenreEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String genre = null;

        genre = entity.getGenre();

        Genre genre1 = new Genre( genre );

        genre1.pk = entity.getPk();

        return genre1;
    }

    @Override
    public GenreEntity toEntity(Genre model) {
        if ( model == null ) {
            return null;
        }

        String genre = null;

        genre = model.getGenre();

        GenreEntity genreEntity = new GenreEntity( genre );

        return genreEntity;
    }
}
