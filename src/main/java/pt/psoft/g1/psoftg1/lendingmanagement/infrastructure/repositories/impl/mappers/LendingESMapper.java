package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookESMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber;
import pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch.LendingES;
import pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch.LendingNumberES;
import pt.psoft.g1.psoftg1.readermanagement.infraestructure.repositories.impl.mappers.ReaderDetailsESMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.CommonESMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch.IsbnES;
import pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch.DescriptionES;

@Mapper(componentModel = "spring", uses = {
        CommonESMapper.class,
        ReaderDetailsESMapper.class,
        BookESMapper.class
})
public interface LendingESMapper {
    Lending toModel(LendingES entity);
    LendingES toEntity(Lending model);

    default LendingNumber mapToLendingNumber(String lendingNumber) {
        if (lendingNumber == null) return null;
        return new LendingNumber(lendingNumber);
    }

    default LendingNumberES map(String lendingNumber) {
        if(lendingNumber == null) return null;
        return new LendingNumberES(lendingNumber);
    }

    default String map(LendingNumber lendingNumber) {
        if (lendingNumber == null) return null;
        return lendingNumber.toString();
    }

    default String map(LendingNumberES lendingNumberES) {
        if(lendingNumberES == null) return null;
        return lendingNumberES.toString();
    }

    // Mapeamento para ISBN
    default IsbnES mapToIsbnES(String isbn) {
        if(isbn == null) return null;
        return new IsbnES(isbn);
    }



    default String mapFromIsbnES(IsbnES isbnES) {
        if(isbnES == null) return null;
        return isbnES.toString();
    }

    // Mapeamento para Description
    default DescriptionES mapToDescriptionES(String description) {
        if(description == null) return null;
        return new DescriptionES(description);
    }

    default String mapFromDescriptionES(DescriptionES descriptionES) {
        if(descriptionES == null) return null;
        return descriptionES.toString();
    }
}