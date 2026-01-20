package com.bookmanagement.validator;

import com.bookmanagement.annotation.ValidISBN;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ISBNValidator implements ConstraintValidator<ValidISBN, String> {
    
    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }
        
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        
        return isValidISBN10(cleanIsbn) || isValidISBN13(cleanIsbn);
    }
    
    private boolean isValidISBN10(String isbn) {
        if (isbn.length() != 10) return false;
        
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(isbn.charAt(i))) return false;
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        
        char lastChar = isbn.charAt(9);
        if (lastChar == 'X') {
            sum += 10;
        } else if (Character.isDigit(lastChar)) {
            sum += (lastChar - '0');
        } else {
            return false;
        }
        
        return sum % 11 == 0;
    }
    
    private boolean isValidISBN13(String isbn) {
        if (isbn.length() != 13) return false;
        
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            if (!Character.isDigit(isbn.charAt(i))) return false;
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        if (!Character.isDigit(isbn.charAt(12))) return false;
        int checkDigit = isbn.charAt(12) - '0';
        int calculatedCheck = (10 - (sum % 10)) % 10;
        
        return checkDigit == calculatedCheck;
    }
}