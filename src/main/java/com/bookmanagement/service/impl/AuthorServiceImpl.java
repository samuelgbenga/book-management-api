package com.bookmanagement.service.impl;

import java.util.List;

import com.bookmanagement.dto.AuthorDTO;
import com.bookmanagement.service.AuthorService;

import lombok.RequiredArgsConstructor;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.Author;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.AuthorMapper;
import com.bookmanagement.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
   public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDTO)
                .toList();
    }

    @Override
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return authorMapper.toDTO(author);
    }

    @Override
     @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        if (authorRepository.findByEmail(authorDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Author with email " + authorDTO.getEmail() + " already exists");
        }
        
        Author author = authorMapper.toEntity(authorDTO);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toDTO(savedAuthor);
    }

    @Override
     @Transactional
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        
        if (authorDTO.getEmail() != null && !authorDTO.getEmail().equals(author.getEmail())) {
            if (authorRepository.findByEmail(authorDTO.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Author with email " + authorDTO.getEmail() + " already exists");
            }
            author.setEmail(authorDTO.getEmail());
        }
        
        if (authorDTO.getName() != null) {
            author.setName(authorDTO.getName());
        }
        
        if (authorDTO.getBio() != null) {
            author.setBio(authorDTO.getBio());
        }
        
        Author updatedAuthor = authorRepository.save(author);
        return authorMapper.toDTO(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        
        if (!author.getBooks().isEmpty()) {
            throw new InvalidOperationException("Cannot delete author with existing books");
        }
        
        authorRepository.deleteById(id);
    }
    
}
