package br.com.stockflow.mapper;

import br.com.stockflow.dto.PersonRequestDTO;
import br.com.stockflow.dto.PersonResponseDTO;
import br.com.stockflow.model.Person;

public final class PersonMapper {

    private PersonMapper() {}

    public static Person toEntity(PersonRequestDTO request) {
        var person = new Person();
        copyToEntity(request, person);
        return person;
    }

    public static void copyToEntity(PersonRequestDTO request, Person person) {
        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setAddress(request.address());
        person.setGender(request.gender());
    }

    public static PersonResponseDTO toResponse(Person person) {
        return new PersonResponseDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getGender()
        );
    }
}