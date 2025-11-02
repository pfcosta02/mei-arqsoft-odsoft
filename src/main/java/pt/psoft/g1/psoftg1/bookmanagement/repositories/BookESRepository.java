package pt.psoft.g1.psoftg1.bookmanagement.repositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookESMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch.BookES;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreESMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("es")
public class BookESRepository implements BookRepository {

    private final ElasticsearchClient client;
    private final BookESMapper mapper;
    private static final String INDEX = "books";

    @Autowired
    public BookESRepository(ElasticsearchClient client, BookESMapper mapper) throws IOException {
        this.client = client;
        this.mapper = mapper;

        boolean exists = client.indices().exists(e -> e.index(INDEX)).value();
        if (!exists) {
            client.indices().create(c -> c.index(INDEX));
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        try {
            SearchResponse<BookES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q.match(t -> t.field("isbn").query(isbn))),
                    BookES.class
            );

            return response.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .map(mapper::toModel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to search book by ISBN in Elasticsearch", e);
        }
    }

    @Override
    public List<BookCountDTO> findTop5BooksLent(LocalDate oneYearAgo, Pageable pageable) {
        return List.of();
    }

    @Override
    public List<Book> findByGenre(String genre) {
        try {
            SearchResponse<BookES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .wildcard(w -> w.field("genre.genre").value("*" + genre + "*"))
                            ),
                    BookES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search books by genre", e);
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        try {
            SearchResponse<BookES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .wildcard(w -> w.field("title").value("*" + title + "*"))
                            ),
                    BookES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search books by title", e);
        }
    }

    @Override
    public List<Book> findByAuthorName(String authorName) {
        try {
            SearchResponse<BookES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .nested(n -> n
                                            .path("authors")
                                            .query(nq -> nq
                                                    .wildcard(w -> w
                                                            .field("authors.name")
                                                            .value("*" + authorName + "*")
                                                    )
                                            )
                                    )
                            ),
                    BookES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search books by author name", e);
        }
    }

    @Override
    public List<Book> findBooksByAuthorNumber(String authorNumber) {
        try {
            SearchResponse<BookES> response = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .nested(n -> n
                                            .path("authors")
                                            .query(nq -> nq
                                                    .term(t -> t
                                                            .field("authors.authorNumber")
                                                            .value(authorNumber)
                                                    )
                                            )
                                    )
                            ),
                    BookES.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search books by author number", e);
        }
    }

    @Override
    public List<Book> searchBooks(pt.psoft.g1.psoftg1.shared.services.Page page, SearchBooksQuery query) {
        try {
            SearchResponse<BookES> response = client.search(s -> {
                s.index(INDEX);

                // Build query with multiple conditions
                s.query(q -> q.bool(b -> {
                    if (query.getTitle() != null && !query.getTitle().isEmpty()) {
                        b.must(m -> m.wildcard(w -> w.field("title").value(query.getTitle() + "*")));
                    }
                    if (query.getGenre() != null && !query.getGenre().isEmpty()) {
                        b.must(m -> m.wildcard(w -> w.field("genre.genre").value(query.getGenre() + "*")));
                    }
                    if (query.getAuthorName() != null && !query.getAuthorName().isEmpty()) {
                        b.must(m -> m.nested(n -> n
                                .path("authors")
                                .query(nq -> nq.wildcard(w -> w
                                        .field("authors.name")
                                        .value(query.getAuthorName() + "*")))
                        ));
                    }
                    return b;
                }));

                s.from((page.getNumber() - 1) * page.getLimit());
                s.size(page.getLimit());
                s.sort(sort -> sort.field(f -> f.field("title.keyword").order(SortOrder.Asc)));

                return s;
            }, BookES.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(mapper::toModel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search books in Elasticsearch", e);
        }
    }



    @Override
    public Book save(Book book) {
        try {
            BookES bookES = mapper.toEntity(book);

            client.index(IndexRequest.of(i -> i
                    .index(INDEX)
                    .id(book.getIsbn().getIsbn())  // Usar ISBN como ID
                    .document(bookES)
                    .refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
            ));

            return book;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save book in Elasticsearch", e);
        }
    }

    @Override
    public void delete(Book book) {
        try {
            client.delete(DeleteRequest.of(d -> d
                    .index(INDEX)
                    .id(book.getIsbn().getIsbn())
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete book in Elasticsearch", e);
        }
    }
}