package pt.psoft.g1.psoftg1.readermanagement.infraestructure.repositories.impl.mappers;

import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.BirthDateES;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.PhoneNumberES;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.ReaderDetailsES;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.ReaderNumberES;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class ReaderDetailsESMapperHelper {

    public static ReaderNumberES toReaderNumberES(ReaderDetails model) {
        try {
            Field field = ReaderDetails.class.getDeclaredField("readerNumber");
            field.setAccessible(true);
            ReaderNumber readerNumber = (ReaderNumber) field.get(model);

            if (readerNumber == null) return null;

            String numberStr = readerNumber.toString();
            String[] parts = numberStr.split("/");
            int year = Integer.parseInt(parts[0].trim());
            int seq = Integer.parseInt(parts[1].trim());
            return new ReaderNumberES(year, seq);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter ReaderNumber", e);
        }
    }

    public static PhoneNumberES toPhoneNumberES(ReaderDetails model) {
        try {
            Field field = ReaderDetails.class.getDeclaredField("phoneNumber");
            field.setAccessible(true);
            PhoneNumber phoneNumber = (PhoneNumber) field.get(model);

            if (phoneNumber == null) return null;
            return new PhoneNumberES(phoneNumber.toString());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter PhoneNumber", e);
        }
    }

    public static BirthDate toBirthDate(ReaderDetailsES entity) {
        BirthDateES birthDateES = entity.getBirthDate();
        if (birthDateES == null) return null;

        LocalDate date = birthDateES.getBirthDate();
        return new BirthDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static ReaderNumber toReaderNumber(ReaderDetailsES entity) {
        String numberStr = entity.getReaderNumber();
        if (numberStr == null) return null;

        String[] parts = numberStr.split("/");
        int year = Integer.parseInt(parts[0].trim());
        int seq = Integer.parseInt(parts[1].trim());
        return new ReaderNumber(year, seq);
    }

    public static PhoneNumber toPhoneNumber(ReaderDetailsES entity) {
        String phoneNumberES = entity.getPhoneNumber();
        if (phoneNumberES == null) return null;

        return new PhoneNumber(phoneNumberES);
    }


}