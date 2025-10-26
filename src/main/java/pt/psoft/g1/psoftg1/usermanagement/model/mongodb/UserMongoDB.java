package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Profile("mongodb")
@Document(collection = "users")
// @EnableMongoAuditing
public class UserMongoDB {

    @Id
    @Getter
    private String userId;

    @Field("version")
    @Version
    private Long version;

    @CreatedDate
    @Field("createdAt")
    @Getter @Setter
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("modifiedAt")
    @Getter @Setter
    private LocalDateTime modifiedAt;

    @Field("createdBy")
    @CreatedBy
    @Getter @Setter
    private String createdBy;

    @Field("modifiedBy")
    @LastModifiedBy
    @Getter @Setter
    private String modifiedBy;

    @Setter @Getter
    private boolean enabled = true;

    @Email
    @NotNull
    @NotBlank
    @Field("username")
    @Getter @Setter
    private String username;

    @Field("password")
    @Getter
    @NotNull
    @NotBlank
    private String password;

    @Getter @Setter
    @Field("name")
    private NameMongoDB name;

    @Getter
    @Field("authorities")
    private Set<Role> authorities = new HashSet<>();

    public UserMongoDB() {}

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