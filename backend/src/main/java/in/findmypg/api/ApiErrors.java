package in.findmypg.api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
class ApiErrors {
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){Map<String,String> fields=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->fields.putIfAbsent(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(Map.of("message","Please check the highlighted fields.","fields",fields));}
    @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(){return ResponseEntity.status(409).body(Map.of("message","That request conflicts with existing information."));}
    @ExceptionHandler(NoSuchElementException.class) ResponseEntity<?> missing(){return ResponseEntity.status(404).body(Map.of("message","The requested item could not be found."));}
}
