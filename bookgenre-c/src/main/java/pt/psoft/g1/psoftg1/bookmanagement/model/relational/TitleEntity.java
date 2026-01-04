package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
@Embeddable
@EqualsAndHashCode
public class TitleEntity implements Serializable
{
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = Title.TITLE_MAX_LENGTH)
    @Column(name = "TITLE", length = Title.TITLE_MAX_LENGTH, nullable = false)
    @Getter
    private String title;

    protected TitleEntity() { }

    public TitleEntity(String title) { this.title = title; }
}
