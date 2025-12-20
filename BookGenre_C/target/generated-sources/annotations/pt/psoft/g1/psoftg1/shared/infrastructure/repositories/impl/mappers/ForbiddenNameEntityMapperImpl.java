package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.relational.ForbiddenNameEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-20T17:10:11+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class ForbiddenNameEntityMapperImpl implements ForbiddenNameEntityMapper {

    @Override
    public ForbiddenName toModel(ForbiddenNameEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String forbiddenName = null;

        forbiddenName = entity.getForbiddenName();

        ForbiddenName forbiddenName1 = new ForbiddenName( forbiddenName );

        return forbiddenName1;
    }

    @Override
    public ForbiddenNameEntity toEntity(ForbiddenName model) {
        if ( model == null ) {
            return null;
        }

        ForbiddenNameEntity forbiddenNameEntity = new ForbiddenNameEntity();

        forbiddenNameEntity.setForbiddenName( model.getForbiddenName() );

        return forbiddenNameEntity;
    }
}
