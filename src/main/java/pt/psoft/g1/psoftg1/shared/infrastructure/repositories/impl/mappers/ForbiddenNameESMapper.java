package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.ForbiddenNameES;

@Mapper(componentModel = "spring")
public interface ForbiddenNameESMapper {

    ForbiddenName toModel(ForbiddenNameES entity);
    ForbiddenNameES toEntity(ForbiddenName model);
}