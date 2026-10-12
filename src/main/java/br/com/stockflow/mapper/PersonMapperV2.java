package br.com.stockflow.mapper;

import br.com.stockflow.dto.v2.PersonRequestDTOv2;
import br.com.stockflow.dto.v2.PersonResponseDTOv2;
import br.com.stockflow.model.Person;

public final class PersonMapperV2 {

    private PersonMapperV2() {}

    public static void copyToEntity(PersonRequestDTOv2 request, Person person) {
        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setAddress(request.address());
        person.setEmail(request.email());
        person.setGender(request.gender());
    }
    public static Person toEntity(PersonRequestDTOv2 request) {
        var person = new Person();
        copyToEntity(request, person);
        return person;
    }

    public static PersonResponseDTOv2 toResponse(Person person) {
        return new PersonResponseDTOv2(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getEmail(),
                person.getGender()
        );
    }
}