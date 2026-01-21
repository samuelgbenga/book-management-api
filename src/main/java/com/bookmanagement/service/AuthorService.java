package com.bookmanagement.service;

import com.bookmanagement.dto.AllAuthorDTO;
import com.bookmanagement.dto.AuthorDTO;
import com.bookmanagement.dto.NewAuthorDTO;

import java.util.List;

public interface AuthorService {

    List<AllAuthorDTO> getAllAuthors();

    AuthorDTO getAuthorById(Long id);

    AuthorDTO createAuthor(NewAuthorDTO authorDTO);

    AuthorDTO updateAuthor(Long id, NewAuthorDTO authorDTO);

    void deleteAuthor(Long id);
}
