package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T11:51:43+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class NameEntityMapperImpl implements NameEntityMapper {

    @Override
    public Name toModel(NameEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String name = null;

        name = entity.getName();

        Name name1 = new Name( name );

        return name1;
    }

    @Override
    public NameEntity toEntity(Name model) {
        if ( model == null ) {
            return null;
        }

        String name = null;

        name = model.getName();

        NameEntity nameEntity = new NameEntity( name );

        return nameEntity;
    }
}
