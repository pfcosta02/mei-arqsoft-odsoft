package pt.psoft.g1.psoftg1.readermanagement.infraestructure.repositories.impl.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch.GenreES;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.ReaderDetailsES;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.CommonESMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch.ReaderES;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReaderDetailsESMapper {

    @Autowired
    private CommonESMapper commonMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * JPA -> Elasticsearch
     */
    public ReaderDetailsES toEntity(ReaderDetails model) {
        if (model == null) return null;

        ReaderDetailsES entity = new ReaderDetailsES();

        // String direto
        entity.setReaderNumber(model.getReaderNumber());
        entity.setPhoneNumber(model.getPhoneNumber());

        // Booleans
        entity.setGdprConsent(model.isGdprConsent());
        entity.setMarketingConsent(model.isMarketingConsent());
        entity.setThirdPartySharingConsent(model.isThirdPartySharingConsent());

        // BirthDate
        if (model.getBirthDate() != null) {
            entity.setBirthDate(commonMapper.map(model.getBirthDate()));
        }

        // Reader
        if (model.getReader() != null) {
            entity.setReader(mapReader(model.getReader()));
        }

        // Photo
        if (model.getPhoto() != null && model.getPhoto().getPhotoFile() != null) {
            entity.setPhoto(model.getPhoto().getPhotoFile());
        }

        // InterestList
        if (model.getInterestList() != null) {
            entity.setInterestList(mapGenres(model.getInterestList()));
        }

        return entity;
    }

    /**
     * Elasticsearch -> JPA
     */
    public ReaderDetails toModel(ReaderDetailsES entity) {
        if (entity == null) return null;

        // Parse ReaderNumber "2025/1" -> get seq
        int seq = parseSeqFromReaderNumber(entity.getReaderNumber());

        // Parse Reader
        Reader reader = null;
        if (entity.getReader() != null) {
            reader = mapReaderES(entity.getReader());
        }

        // Parse BirthDate -> formato yyyy-MM-dd
        String birthDateStr = formatBirthDate(entity.getBirthDate());

        // PhoneNumber
        String phoneNumber = entity.getPhoneNumber();

        // Parse InterestList
        List<Genre> genres = null;
        if (entity.getInterestList() != null) {
            genres = mapGenresES(entity.getInterestList());
        }

        // Construir ReaderDetails
        return new ReaderDetails(
                seq,
                reader,
                birthDateStr,  // ✅ Formato yyyy-MM-dd
                phoneNumber,
                entity.isGdprConsent(),
                entity.isMarketingConsent(),
                entity.isThirdPartySharingConsent(),
                entity.getPhoto(),
                genres
        );
    }

    // ===== Helper Methods =====

    private int parseSeqFromReaderNumber(String numberStr) {
        if (numberStr == null || numberStr.isEmpty()) {
            throw new IllegalArgumentException("ReaderNumber cannot be null");
        }
        try {
            String[] parts = numberStr.split("/");
            return Integer.parseInt(parts[1].trim());  // Retorna apenas o seq
        } catch (Exception e) {
            throw new RuntimeException("Invalid readerNumber format: " + numberStr, e);
        }
    }

    /**
     * Converte BirthDateES para String no formato yyyy-MM-dd
     */
    private String formatBirthDate(pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.BirthDateES birthDateES) {
        if (birthDateES == null) return null;

        LocalDate date = birthDateES.getBirthDate();
        if (date == null) return null;

        // Formato yyyy-MM-dd
        return date.format(DATE_FORMATTER);
    }

    private ReaderES mapReader(Reader reader) {
        if (reader == null) return null;

        ReaderES readerES = new ReaderES(reader.getUsername(), reader.getPassword());
        readerES.setEnabled(reader.isEnabled());

        if (reader.getName() != null) {
            readerES.setName(reader.getName().toString());
        }

        return readerES;
    }

    private Reader mapReaderES(ReaderES readerES) {
        if (readerES == null) return null;

        Reader reader = new Reader(
                readerES.getUsername(),
                readerES.getPassword()
        );

        reader.setEnabled(readerES.isEnabled());

        if (readerES.getName() != null) {
            reader.setName(readerES.getName().getName());
        }

        return reader;
    }

    private List<GenreES> mapGenres(List<Genre> genres) {
        if (genres == null) return null;
        return genres.stream()
                .map(g -> new GenreES(g.getGenre()))
                .collect(Collectors.toList());
    }

    private List<Genre> mapGenresES(List<GenreES> genresES) {
        if (genresES == null) return null;
        return genresES.stream()
                .map(g -> new Genre(g.getGenre()))
                .collect(Collectors.toList());
    }
}