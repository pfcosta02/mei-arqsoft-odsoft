package pt.psoft.g1.psoftg1.shared.repositories.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import java.io.IOException;

@Repository
@Profile("es")
public class PhotoESRepository implements PhotoRepository {

    private final ElasticsearchClient client;
    private static final String INDEX = "photos";

    @Autowired
    public PhotoESRepository(ElasticsearchClient client) throws IOException {
        this.client = client;

        // Criar índice se não existir
        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public void deleteByPhotoFile(String photoFile) {
        if (photoFile == null || photoFile.isEmpty()) {
            return;
        }

        try {
            // Delete all documents with the given photoFile
            DeleteByQueryResponse response = client.deleteByQuery(d -> d
                    .index(INDEX)
                    .query(q -> q
                            .term(t -> t
                                    .field("photoFile")
                                    .value(photoFile))));

            // Optionally log the number of deleted documents
            System.out.println("Deleted " + response.deleted() + " photo(s) with file: " + photoFile);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar Photo por photoFile no Elasticsearch", e);
        }
    }
}