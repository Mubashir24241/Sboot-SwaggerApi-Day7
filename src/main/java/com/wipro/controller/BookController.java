package com.wipro.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wipro.entity.Book;
import com.wipro.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "Get all books", description = "Fetches a list of all books from the database")
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Add a new book", description = "Adds a new book to the database")
    @ApiResponse(responseCode = "200", description = "Book added successfully")
    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        Optional<Book> existingBook = bookService.getBookById(book.getId());
        if (existingBook.isPresent()) {
            return ResponseEntity.status(400).body("Book with ID " + book.getId() + " already exists. Please choose a different ID.");
        }

        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok("Book added successfully: " + savedBook);
    }

    @Operation(summary = "Get a book by ID", description = "Fetches a book by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        
        if (!book.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book with ID " + id + " is not found. Please verify the book ID.");
        }
        
        return ResponseEntity.ok(book.get());
    }

    @Operation(summary = "Update an existing book", description = "Updates the details of an existing book")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Optional<Book> updatedBook = bookService.updateBook(id, bookDetails);
        return updatedBook.map(book -> ResponseEntity.ok("Book updated successfully: " + book))
                .orElseGet(() -> ResponseEntity.badRequest().body("No book found with ID: " + id + ". Kindly add it first."));
    }

    @Operation(summary = "Delete a book by ID", description = "Deletes a book from the database by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        boolean isDeleted = bookService.deleteBook(id);
        if (isDeleted) {
            return ResponseEntity.ok("Book with ID " + id + " deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("No book found with ID " + id + " so nothing to delete.");
        }
    }

    @Operation(summary = "Get paginated and sorted books", description = "Fetches books with pagination and sorting")
    @GetMapping("/paginated")
    public ResponseEntity<Page<Book>> getPaginatedAndSortedBooks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Page<Book> books = bookService.getBooksPaginatedAndSorted(page, size, sortBy, direction);
        return ResponseEntity.ok(books);
    }
}
