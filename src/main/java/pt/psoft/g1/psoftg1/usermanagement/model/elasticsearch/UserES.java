package pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.NameES;
import pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch.RoleES;

/**
 * Elasticsearch version of User entity
 */
@Getter
@Setter
@Document(indexName = "users")
public class UserES implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @CreatedDate
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field(type = FieldType.Date)
    private LocalDateTime modifiedAt;

    @CreatedBy
    @Field(type = FieldType.Keyword)
    private String createdBy;

    @LastModifiedBy
    @Field(type = FieldType.Keyword)
    private String modifiedBy;

    @Field(type = FieldType.Boolean)
    private boolean enabled = true;

    @Email
    @NotNull
    @NotBlank
    @Field(type = FieldType.Keyword)
    private String username;

    @NotNull
    @NotBlank
    @Field(type = FieldType.Keyword)
    private String password;

    @JsonProperty("name")
    @Field(type = FieldType.Object)
    private NameES name;

    @Field(type = FieldType.Nested)
    private final Set<RoleES> authorities = new HashSet<>();

    @Field(type = FieldType.Boolean)
    private Boolean accountNonLocked;

    @Field(type = FieldType.Boolean)
    private Boolean accountNonExpired;

    @Field(type = FieldType.Boolean)
    private Boolean credentialsNonExpired;

    protected UserES() {
        // for deserialization
    }

    public UserES(final String username, final String password) {
        this.username = username;
        setPassword(password);
    }

    public static UserES newUser(final String username, final String password, final String name) {
        final var u = new UserES(username, password);
        u.setName(name);
        return u;
    }

    public static UserES newUser(final String username, final String password, final String name, final String role) {
        final var u = new UserES(username, password);
        u.setName(name);
        u.addAuthority(new RoleES(role));
        return u;
    }

    public void setPassword(final String password) {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void addAuthority(final RoleES r) {
        authorities.add(r);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    public void setName(String name) {
        this.name = new NameES(name);
    }

    @JsonSetter("name")
    public void setName(NameES name) {
        this.name = name;
    }
}