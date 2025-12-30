package pt.psoft.g1.psoftg1.authormanagement.services;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.BioEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.NameEntityMapper;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:59:33+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class AuthorMapperImpl extends AuthorMapper {

    @Autowired
    private BioEntityMapper bioEntityMapper;
    @Autowired
    private NameEntityMapper nameEntityMapper;

    @Override
    public Author create(CreateAuthorRequest request) {
        if ( request == null ) {
            return null;
        }

        Author author = new Author();

        author.setPhoto( map( request.getPhoto() ) );
        author.setName( nameEntityMapper.map( request.getName() ) );
        author.setBio( bioEntityMapper.map( request.getBio() ) );

        return author;
    }

    @Override
    public void update(UpdateAuthorRequest request, Author author) {
        if ( request == null ) {
            return;
        }

        author.setPhoto( map( request.getPhoto() ) );
        author.setName( nameEntityMapper.map( request.getName() ) );
        author.setBio( bioEntityMapper.map( request.getBio() ) );
    }
}
