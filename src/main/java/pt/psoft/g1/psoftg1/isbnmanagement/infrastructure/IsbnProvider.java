package pt.psoft.g1.psoftg1.isbnmanagement.infrastructure;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;


public interface IsbnProvider {

    Isbn searchByTitle(String title);

}





