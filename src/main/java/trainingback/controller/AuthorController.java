package trainingback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trainingback.dto.AuthorDTO;
import trainingback.exception.ModelNotFoundException;
import trainingback.service.AuthorService;
import trainingback.util.NullChecker;

import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthors(){
        List<AuthorDTO> authorList=authorService.getAllAuthors();
        return new ResponseEntity<>(authorList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthorsInfoById(@PathVariable("id") Long authorID){
        try{
            if (authorID==null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            AuthorDTO authorDTO=this.authorService.getInfoByID(authorID);
            return new ResponseEntity<>(authorDTO,HttpStatus.OK);
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateAuthor(@RequestBody AuthorDTO authorDTO){
        try {
            if (NullChecker.almostOneFieldIsNull(authorDTO)){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            this.authorService.upgradeInfo(authorDTO);
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
    public ResponseEntity<?> delete(@PathVariable("id") Long authorID){
        try{
            if (authorID==null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            this.authorService.delete(authorID);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (ModelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());}
    }
}

