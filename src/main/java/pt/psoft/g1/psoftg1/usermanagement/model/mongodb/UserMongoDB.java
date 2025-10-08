package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Profile("mongodb")
@Primary
@Document(collection = "users")
@EnableMongoAuditing
public class UserMongoDB {

    @Id
    @GeneratedValue
    @Column(name="USER_ID")
    private Long id;

    @Field("version")
    @Version
    private Long version;

    @CreatedDate
    @Field("createdAt")
    @Getter
    @Setter
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("modifiedAt")
    @Getter
    @Setter
    private LocalDateTime modifiedAt;

    @Field("createdBy")
    @CreatedBy
    @Getter
    @Setter
    private String createdBy;

    @Field("modifiedBy")
    @LastModifiedBy
    @Getter
    @Setter
    private String modifiedBy;

    @Setter
    @Getter
    private boolean enabled = true;

    @Setter
    @Email
    @Getter
    @NotNull
    @NotBlank
    @Field("username")
    private String username;

    @Field("password")
    @Getter
    @NotNull
    @NotBlank
    private String password;

    @Getter
    @Setter
    @Field("name")
    private NameMongoDB name;

    @ElementCollection
    @Getter
    @Field("authorities")
    private final Set<Role> authorities = new HashSet<>();

    protected UserMongoDB() {}

    public UserMongoDB(String username, String password, Role role)
    {
        this.username = username;
        this.password = password;
        this.authorities.add(role);
    }

    public UserMongoDB(String username, String password, NameMongoDB name, Role role)
    {
        this(username, password, role);
        this.name = name;
    }
}
