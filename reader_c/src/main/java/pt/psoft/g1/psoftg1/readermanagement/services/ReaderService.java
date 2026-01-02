package pt.psoft.g1.psoftg1.readermanagement.services;

import java.util.List;
import java.util.Optional;

import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;
import pt.psoft.g1.psoftg1.readermanagement.dto.UserDTO;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.shared.services.Page;

public interface ReaderService {
    ReaderDetails create(CreateReaderRequest request, String photoURI);

    ReaderDetails update(String id, UpdateReaderRequest request, long desireVersion, String photoURI);

    Optional<ReaderDetails> removeReaderPhoto(String readerNumber, long desiredVersion);

    List<ReaderDetails> searchReaders(Page page, SearchReadersQuery query);

    void persistTemporary(UserDTO userDTO);

    List<ReaderDetailsDTO> readersToDTO();
}