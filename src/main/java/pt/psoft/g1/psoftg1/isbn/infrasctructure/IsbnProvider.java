package pt.psoft.g1.psoftg1.isbn.infrasctructure;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;

import java.util.List;


public interface IsbnProvider {

    Isbn searchByTitle(String title);

}





