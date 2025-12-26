package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
@Entity
@Table(name = "readers")
public class ReaderEntity {
    @Id
    private String readerId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    protected ReaderEntity() {
        // for ORM only
    }

    public ReaderEntity(String readerId, String userId, String name, String email) {
        this.readerId = readerId;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getReaderId() { return readerId; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
