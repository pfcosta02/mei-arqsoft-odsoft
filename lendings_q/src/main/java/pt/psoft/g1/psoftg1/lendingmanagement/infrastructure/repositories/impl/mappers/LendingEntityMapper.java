package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingNumberEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { ReaderDetailsEntityMapper.class, BookEntityMapper.class})
public interface LendingEntityMapper {

    // ⭐ MAPPING: Entity → Model (Domain)
    // Usa reflexão para settar pk e version que não estão no @Builder
    default Lending toModel(LendingEntity entity) {
        if (entity == null) {
            return null;
        }

        // 1. Cria o Lending com @Builder
        // ⭐ IMPORTANTE: MapStruct mapeia automaticamente BookEntity → Book e ReaderDetailsEntity → ReaderDetails
        // porque estão declarados em uses = { ReaderDetailsEntityMapper.class, BookEntityMapper.class}
        Lending lending = Lending.builder()
                .book(entity.getBook() != null ? bookEntityMapperToModel(entity.getBook()) : null)
                .readerDetails(entity.getReaderDetails() != null ? readerDetailsEntityMapperToModel(entity.getReaderDetails()) : null)
                .lendingNumber(mapLendingNumber(entity.getLendingNumber()))
                .startDate(entity.getStartDate())
                .limitDate(entity.getLimitDate())
                .returnedDate(entity.getReturnedDate())
                .fineValuePerDayInCents(entity.getFineValuePerDayInCents())
                .commentary(entity.getCommentary())
                .rating(entity.getRating())
                .build();

        // 2. ⭐ Seta manualmente pk e version via reflexão
        setFieldValue(lending, "pk", entity.getPk());
        setFieldValue(lending, "version", entity.getVersion());

        return lending;
    }

    // ⭐ Métodos auxiliares para mapear as entidades relacionadas
    // Estes são chamados automaticamente pelos mappers injectados
    default pt.psoft.g1.psoftg1.bookmanagement.model.Book bookEntityMapperToModel(
            pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity entity) {
        // MapStruct vai gerar isto automaticamente via BookEntityMapper
        return null;  // Será implementado pelo MapStruct
    }

    default pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails readerDetailsEntityMapperToModel(
            pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity entity) {
        // MapStruct vai gerar isto automaticamente via ReaderDetailsEntityMapper
        return null;  // Será implementado pelo MapStruct
    }

    // ⭐ MAPPING: Model → Entity (para criar nova entidade)
    // NÃO mapeia Book e ReaderDetails - deixa o repositório fazer isso
    default LendingEntity toEntity(Lending model) {
        if (model == null) {
            return null;
        }

        return new LendingEntity(
                null,  // Book será setado no repositório
                null,  // ReaderDetails será setado no repositório
                mapLendingNumber(model.getLendingNumberObj()),
                model.getStartDate(),
                model.getLimitDate(),
                model.getReturnedDate(),
                model.getFineValuePerDayInCents(),
                model.getCommentary(),
                model.getRating()
        );
    }

    // ⭐ Método auxiliar para reflexão
    default void setFieldValue(Lending lending, String fieldName, Object value) {
        try {
            var field = Lending.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(lending, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    // ⭐ Métodos auxiliares para mapear tipos complexos
    default LendingNumber mapLendingNumber(LendingNumberEntity entity) {
        return entity == null ? null : new LendingNumber(entity.getLendingNumber());
    }

    default LendingNumberEntity mapLendingNumber(LendingNumber number) {
        return number == null ? null : new LendingNumberEntity(number.getLendingNumber());
    }

    default LendingNumber mapLendingNumberFromString(String value) {
        return value == null ? null : new LendingNumber(value);
    }

    default Integer mapOptionalInteger(Optional<Integer> value) {
        return value == null ? null : value.orElse(null);
    }

    default Optional<Integer> mapIntegerToOptional(Integer value) {
        return Optional.ofNullable(value);
    }

    default String mapNameEntity(NameEntity value) {
        return value == null ? null : value.getName();
    }
}