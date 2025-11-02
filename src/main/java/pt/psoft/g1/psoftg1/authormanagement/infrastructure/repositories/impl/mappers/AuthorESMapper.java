package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.elasticsearch.AuthorES;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoMapperES;

@Mapper(componentModel = "spring", uses = { PhotoMapperES.class })
public interface AuthorESMapper {

    default AuthorES toEntity(Author author) {
        if (author == null) return null;

        AuthorES entity = new AuthorES();

        entity.setName(author.getName().getName());
        entity.setBio(author.getBio().getBio());
        entity.setAuthorNumber(author.getAuthorNumber());
        entity.setVersion(author.getVersion());

        if (author.getPhoto() != null && author.getPhoto().getPhotoFile() != null) {
            entity.setPhoto(author.getPhoto().getPhotoFile());
        }

        return entity;
    }

    default Author toModel(AuthorES entity) {
        if (entity == null) return null;

        String photoFile = entity.getPhotoFile();

        return new Author(
                entity.getName(),
                entity.getBio(),
                photoFile
        );
    }
}