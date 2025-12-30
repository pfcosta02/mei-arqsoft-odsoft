package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.BioEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:59:33+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class BioEntityMapperImpl implements BioEntityMapper {

    @Override
    public Bio toModel(BioEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String bio = null;

        bio = entity.getBio();

        Bio bio1 = new Bio( bio );

        return bio1;
    }

    @Override
    public BioEntity toEntity(Bio model) {
        if ( model == null ) {
            return null;
        }

        String bio = null;

        bio = model.getBio();

        BioEntity bioEntity = new BioEntity( bio );

        return bioEntity;
    }
}
