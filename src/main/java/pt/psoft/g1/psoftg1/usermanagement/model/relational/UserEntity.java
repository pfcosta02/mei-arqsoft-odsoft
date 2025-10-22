package pt.psoft.g1.psoftg1.usermanagement.model.relational;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Entity
@Table(name = "T_USER")
@EntityListeners(AuditingEntityListener.class)
@Profile("jpa")
@Primary
public class UserEntity
{
    @Id
    @GeneratedValue
    @Column(name="USER_ID")
    @Getter
    private Long id;

    @Version
    private Long version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Getter
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Getter
    private LocalDateTime modifiedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    @Getter
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    @Setter
    @Getter
    private boolean enabled = true;

    @Setter
    @Column(unique = true, /*updatable = false,*/ nullable = false)
    @Email
    @Getter
    @NotNull
    @NotBlank
    private String username;

    @Column(nullable = false)
    @Getter
    @NotNull
    @NotBlank
    private String password;

    @Getter
    @Setter
    @Embedded
    private NameEntity name;

    @ElementCollection
    @CollectionTable(name = "AUTHOR_AUTHORITIES")
    @Getter
    private final Set<Role> authorities = new HashSet<>();

    public UserEntity() {}

    public UserEntity(String username, String password, Role role)
    {
        this.username = username;
        this.password = password;
        this.authorities.add(role);
    }

    public UserEntity(String username, String password, NameEntity name, Role role)
    {
        this(username, password, role);
        this.name = name;
    }
}
