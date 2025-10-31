package pt.psoft.g1.psoftg1.isbnmanagement.infrasctructure;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;


public interface IsbnProvider {

    Isbn searchByTitle(String title);

}





