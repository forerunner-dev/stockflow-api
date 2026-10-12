package br.com.stockflow.controllers;



import br.com.stockflow.dto.v2.PersonRequestDTOv2;
import br.com.stockflow.dto.v2.PersonResponseDTOv2;
import br.com.stockflow.services.PersonServices;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/v2/person")
public class PersonControllerV2 {


    private final PersonServices personServices;

    public PersonControllerV2(PersonServices personServices) {
        this.personServices = personServices;
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonResponseDTOv2 findById(@PathVariable("id") Long id) {
        return personServices.findByIdV2(id);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonResponseDTOv2> findAll() {

        return personServices.findAllV2();

    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDTOv2> create(@RequestBody @Valid PersonRequestDTOv2 request) {
        var created = personServices.createV2(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonResponseDTOv2 update(@PathVariable Long id,
                                    @RequestBody
                                    @Valid PersonRequestDTOv2 request) {

        return personServices.updateV2(id, request);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        personServices.delete(id);
        return ResponseEntity.noContent().build();
    }


}
