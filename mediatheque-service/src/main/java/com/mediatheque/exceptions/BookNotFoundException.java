package com.mediatheque.exceptions;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(Long id){
        super("No books found with id" + id);
    }
}
