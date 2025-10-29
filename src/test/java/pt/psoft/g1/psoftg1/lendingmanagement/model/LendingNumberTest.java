package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LendingNumberTest {
    @Test
    void ensureLendingNumberNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(null));
    }
    @Test
    void ensureLendingNumberNotBlank(){
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(""));
    }
    @Test
    void ensureLendingNumberNotWrongFormat(){
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("1/2025"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("25/1"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2025-1"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2025\\1"));
    }
    @Test
    void ensureLendingNumberIsSetWithString() {
        final var ln = new LendingNumber("2025/1");
        assertEquals("2025/1", ln.toString());
    }

    @Test
    void ensureLendingNumberIsSetWithSequential() {
        final LendingNumber ln = new LendingNumber(1);
        assertNotNull(ln);
        assertEquals(LocalDate.now().getYear() + "/1", ln.toString());
    }

    @Test
    void ensureLendingNumberIsSetWithYearAndSequential() {
        final LendingNumber ln = new LendingNumber(2025,1);
        assertNotNull(ln);
    }

    @Test
    void ensureSequentialCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(2025, -1));
    }

    @Test
    void ensureYearCannotBeInTheFuture() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(LocalDate.now().getYear()+1,1));
    }

    /* =========================================================== NOVOS TESTES =========================================================== */

    @Test
    void ensureYearCannotBeBefore1970() 
    {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(1969, 1));
    }

    
    @Test
    void ensureYear1970IsValid() 
    {
        final LendingNumber ln = new LendingNumber(1970, 0);
        assertEquals("1970/0", ln.toString());
    }

    
    void ensureCurrentYearIsValid() 
    {
        int currentYear = LocalDate.now().getYear();
        final LendingNumber ln = new LendingNumber(currentYear, 5);
        assertEquals(currentYear + "/5", ln.toString());
    }


    @Test
    void ensureLendingNumberToStringWithYearAndSequential() 
    {
        final LendingNumber ln = new LendingNumber(2025, 1);
        assertEquals("2025/1", ln.toString());
    }

}