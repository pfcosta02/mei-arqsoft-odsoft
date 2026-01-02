package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderTempEntity;
import pt.psoft.g1.psoftg1.shared.model.Name;

@Mapper(componentModel = "spring")
public interface ReaderEntityMapper {
    Reader toModel(ReaderEntity entity);

    ReaderEntity toEntity(Reader reader);

    /* TEMPORARIO */
    Reader toModelFromTemp(ReaderTempEntity entity);

    ReaderTempEntity toTempEntity(Reader reader);

    default String map(Name value)
    {
        return value == null ? null : value.getName();
    }
}