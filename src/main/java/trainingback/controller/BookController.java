package trainingback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trainingback.dto.BookDTO;
import trainingback.exception.ModelAlreadyExistsException;
import trainingback.exception.ModelNotFoundException;
import trainingback.service.BookService;
import trainingback.util.NullChecker;


import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookDTO book ){
        try{
            if (NullChecker.almostOneFieldIsNull(book.getAuthor())|| book.getYear()==null|| book.getGenre()==null || book.getTitle()==null || book.getAuthor().getId()<0){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            this.bookService.create(book);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ModelAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalAccessException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllBooks(@RequestParam(value = "completed") boolean completed){
        List<BookDTO> bookDTOS=this.bookService.getAllBooks(completed);
        return new ResponseEntity<>(bookDTOS,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookInfoByID(@PathVariable("id") Long id){
        try{
            if (id==null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            BookDTO bookDTO=this.bookService.getInfoByID(id);
            return new ResponseEntity<>(bookDTO,HttpStatus.OK);
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping()
    public ResponseEntity<?> updateBook(@RequestBody BookDTO bookDTO){
        try {
            if (NullChecker.almostOneFieldIsNull(bookDTO) || NullChecker.almostOneFieldIsNull(bookDTO.getAuthor())){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            this.bookService.upgradeInfo(bookDTO);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long bookID){
        try{
            if (bookID==null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            this.bookService.delete(bookID);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());}
    }
}
