package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.BioEntity;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.NameEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T11:51:43+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class AuthorEntityMapperImpl implements AuthorEntityMapper {

    @Autowired
    private NameEntityMapper nameEntityMapper;
    @Autowired
    private BioEntityMapper bioEntityMapper;
    @Autowired
    private PhotoEntityMapper photoEntityMapper;

    @Override
    public Author toModel(AuthorEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Author author = new Author();

        author.setPhoto( photoEntityMapper.map( entity.getPhoto() ) );
        author.setName( nameEntityMapper.toModel( entity.getName() ) );
        author.setBio( bioEntityMapper.toModel( entity.getBio() ) );
        author.authorNumber = entity.getAuthorNumber();

        return author;
    }

    @Override
    public AuthorEntity toEntity(Author model) {
        if ( model == null ) {
            return null;
        }

        NameEntity name = null;
        BioEntity bio = null;

        name = nameEntityMapper.toEntity( model.getName() );
        bio = bioEntityMapper.toEntity( model.getBio() );

        PhotoEntity photoURI = null;

        AuthorEntity authorEntity = new AuthorEntity( name, bio, photoURI );

        authorEntity.setPhoto( photoEntityMapper.toEntity( model.getPhoto() ) );

        return authorEntity;
    }
}
