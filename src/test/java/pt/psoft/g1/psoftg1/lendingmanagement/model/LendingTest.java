package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@PropertySource({"classpath:config/library.properties"})
class LendingTest {
    private static final ArrayList<Author> authors = new ArrayList<>();
    private static Book mockBook;
    private static ReaderDetails mockReaderDetails;
    @Value("${lendingDurationInDays}")
    private int lendingDurationInDays;
    @Value("${fineValuePerDayInCents}")
    private int fineValuePerDayInCents;
    
    @BeforeAll
    public static void setup()
    {
        Author mockAuthor = mock(Author.class);
        authors.add(mockAuthor);
        
        mockBook = mock(Book.class);
        mockReaderDetails = mock(ReaderDetails.class);
    }

    @Test
    void ensureBookNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Lending(null, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents));
    }

    @Test
    void ensureReaderNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Lending(mockBook, null, 1, lendingDurationInDays, fineValuePerDayInCents));
    }

    @Test
    void ensureValidReaderNumber(){
        assertThrows(IllegalArgumentException.class, () -> new Lending(mockBook, mockReaderDetails, -1, lendingDurationInDays, fineValuePerDayInCents));
    }

    @Test
    void testSetReturned(){
        // Arrange
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);

        // Act
        lending.setReturned(0,null);
        
        // Assert
        assertEquals(LocalDate.now(), lending.getReturnedDate());
    }

    @Test
    void testGetDaysDelayed(){
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(0, lending.getDaysDelayed());
    }

    @Test
    void testGetDaysUntilReturn(){
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(Optional.of(lendingDurationInDays), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysOverDue(){
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(Optional.empty(), lending.getDaysOverdue());
    }

    @Test
    void testGetLendingNumber() {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(LocalDate.now().getYear() + "/1", lending.getLendingNumber());
    }

    @Test
    void testGetBook() {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(mockBook, lending.getBook());
    }

    @Test
    void testGetReaderDetails() {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(mockReaderDetails, lending.getReaderDetails());
    }

    @Test
    void testGetStartDate() {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(LocalDate.now(), lending.getStartDate());
    }

    @Test
    void testGetLimitDate() {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertEquals(LocalDate.now().plusDays(lendingDurationInDays), lending.getLimitDate());
    }

    @Test
    void testGetReturnedDate() {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertNull(lending.getReturnedDate());
    }

    /* =========================================================== NOVOS TESTES =========================================================== */

    @Test
    void testGetDaysUntilReturnAfterReturn() 
    {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        lending.setReturned(0, null);
       
        // Assert
        assertEquals(Optional.ofNullable(null), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysOverdueAfterDelay() 
    {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, -5, fineValuePerDayInCents); // limite já passou
        
        // Assert
        assertTrue(lending.getDaysOverdue().isPresent());
        assertEquals(5, lending.getDaysOverdue().get());
    }

    @Test
    void testGetFineValueInCentsAfterDelay() 
    {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, -3, fineValuePerDayInCents);
        
        // Assert
        assertEquals(Optional.of(3 * fineValuePerDayInCents), lending.getFineValueInCents());
    }

    @Test
    void testSetReturnedStaleObject() 
    {
        // Act
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Assert
        assertThrows(org.hibernate.StaleObjectStateException.class, () -> lending.setReturned(1, null));
    }

    @Test
    void testSetReturnedTwice() 
    {
        // Arrange
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        
        // Act
        lending.setReturned(0, null);
        
        // Assert
        assertThrows(IllegalArgumentException.class, () -> lending.setReturned(0, null));
    }

    @Test
    void testNewBootstrappingLending() 
    {
        // Act
        Lending lending = Lending.newBootstrappingLending(
            mockBook,
            mockReaderDetails,
            2025,
            1,
            LocalDate.of(2025, 1, 1),
            null,
            10,
            50
        );

        // Assert
        assertEquals(mockBook, lending.getBook());
        assertEquals(mockReaderDetails, lending.getReaderDetails());
        assertEquals("2025/1", lending.getLendingNumber());
        assertEquals(LocalDate.of(2025, 1, 11), lending.getLimitDate());
        assertEquals(50, lending.getFineValuePerDayInCents());
    }
    
    @Test
    void testSetReturnedWithCommentary() 
    {
        // Arrange
        Lending lending = new Lending(mockBook, mockReaderDetails, 1, lendingDurationInDays, fineValuePerDayInCents);

        // Act
        lending.setReturned(0, "Livro devolvido com atraso");
        
        // Assert
        assertEquals("Livro devolvido com atraso", lending.getCommentary());
    }



}
