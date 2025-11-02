package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.BirthDateES;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.NameES;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.PhotoES;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CommonESMapper {

    // --- BirthDate conversions ---

    // BirthDate (JPA) -> BirthDateES (Elasticsearch)
    default BirthDateES map(BirthDate birthDate) {
        if (birthDate == null) return null;
        LocalDate date = birthDate.getBirthDate();
        return new BirthDateES(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    // BirthDateES (Elasticsearch) -> BirthDate (JPA)
    default BirthDate map(BirthDateES birthDateES) {
        if (birthDateES == null) return null;
        LocalDate date = birthDateES.getBirthDate();
        return new BirthDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    // BirthDateES -> String (for when ReaderDetailsES has birthDate as String)
    default String birthDateESToString(BirthDateES birthDateES) {
        if (birthDateES == null) return null;
        return birthDateES.toString();
    }

    // String -> BirthDateES (for when mapping back from String)
    default BirthDateES stringToBirthDateES(String birthDateStr) {
        if (birthDateStr == null) return null;
        return new BirthDateES(birthDateStr);
    }

    // --- Name conversions ---
    default String map(NameES nameES) {
        return nameES == null ? null : nameES.toString();
    }

    default String map(Name name) {
        return name == null ? null : name.toString();
    }
}