package pt.psoft.g1.psoftg1.isbn.infrasctructure;
import pt.psoft.g1.psoftg1.isbn.model.BookInfo;

import java.util.List;


public interface IsbnProvider {

    List<BookInfo> searchByTitle(String title);

}





