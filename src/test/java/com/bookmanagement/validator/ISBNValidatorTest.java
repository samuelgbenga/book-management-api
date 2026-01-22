package com.bookmanagement.validator;

import com.bookmanagement.annotation.ValidISBN;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ISBNValidator Tests")
class ISBNValidatorTest {
    
    private ISBNValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;
    
    @BeforeEach
    void setUp() {
        validator = new ISBNValidator();
    }
    
    // ==================== ISBN-13 TESTS ====================
    
    @Test
    @DisplayName("Should validate correct ISBN-13 with hyphens")
    void testValidISBN13_WithHyphens() {
        assertThat(validator.isValid("978-0-13-235088-4", context)).isTrue();
    }
    
    @Test
    @DisplayName("Should validate correct ISBN-13 without hyphens")
    void testValidISBN13_WithoutHyphens() {
        assertThat(validator.isValid("9780132350884", context)).isTrue();
    }
    
    @Test
    @DisplayName("Should validate correct ISBN-13 with spaces")
    void testValidISBN13_WithSpaces() {
        assertThat(validator.isValid("978 0 13 235088 4", context)).isTrue();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "978-0132350884",
        "9780132350884",
        "978-0596007126",
        "9780596007126",
        "978-1491950296",
        "9781491950296"
    })
    @DisplayName("Should validate multiple correct ISBN-13 numbers")
    void testValidISBN13_MultipleValid(String isbn) {
        assertThat(validator.isValid(isbn, context)).isTrue();
    }
    
    @Test
    @DisplayName("Should reject ISBN-13 with invalid checksum")
    void testInvalidISBN13_BadChecksum() {
        assertThat(validator.isValid("978-0132350885", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject ISBN-13 with wrong length")
    void testInvalidISBN13_WrongLength() {
        assertThat(validator.isValid("978013235088", context)).isFalse();
    }
    
    // ==================== ISBN-10 TESTS ====================
    
    @Test
    @DisplayName("Should validate correct ISBN-10 with hyphens")
    void testValidISBN10_WithHyphens() {
        assertThat(validator.isValid("0-13-235088-2", context)).isTrue();
    }
    
    @Test
    @DisplayName("Should validate correct ISBN-10 without hyphens")
    void testValidISBN10_WithoutHyphens() {
        assertThat(validator.isValid("0132350882", context)).isTrue();
    }
    
    @Test
    @DisplayName("Should validate ISBN-10 with X checksum")
    void testValidISBN10_WithXChecksum() {
        assertThat(validator.isValid("043942089X", context)).isTrue();
        assertThat(validator.isValid("0-439-42089-X", context)).isTrue();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "0132350882",
        "0-13-235088-2",
        "043942089X",
        "0596007124"
    })
    @DisplayName("Should validate multiple correct ISBN-10 numbers")
    void testValidISBN10_MultipleValid(String isbn) {
        assertThat(validator.isValid(isbn, context)).isTrue();
    }
    
    @Test
    @DisplayName("Should reject ISBN-10 with invalid checksum")
    void testInvalidISBN10_BadChecksum() {
        assertThat(validator.isValid("0132350883", context)).isFalse();
    }
    
    // ==================== EDGE CASES ====================
    
    @Test
    @DisplayName("Should reject null ISBN")
    void testInvalidISBN_Null() {
        assertThat(validator.isValid(null, context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject empty ISBN")
    void testInvalidISBN_Empty() {
        assertThat(validator.isValid("", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject blank ISBN")
    void testInvalidISBN_Blank() {
        assertThat(validator.isValid("   ", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject ISBN with too few digits")
    void testInvalidISBN_TooShort() {
        assertThat(validator.isValid("123", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject ISBN with too many digits")
    void testInvalidISBN_TooLong() {
        assertThat(validator.isValid("12345678901234", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject ISBN with non-numeric characters")
    void testInvalidISBN_NonNumeric() {
        assertThat(validator.isValid("abcd-efgh-ijkl", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject ISBN-10 with X in wrong position")
    void testInvalidISBN10_XInWrongPosition() {
        assertThat(validator.isValid("X132350882", context)).isFalse();
    }
    
    @Test
    @DisplayName("Should reject ISBN with mixed valid and invalid characters")
    void testInvalidISBN_MixedCharacters() {
        assertThat(validator.isValid("978-ABC-123", context)).isFalse();
    }
}