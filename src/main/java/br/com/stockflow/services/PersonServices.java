package br.com.stockflow.services;

import br.com.stockflow.exception.ResourceNotFoundException;
import br.com.stockflow.model.Person;
import br.com.stockflow.repository.PersonRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PersonServices {

    private static final Logger logger = LoggerFactory.getLogger(PersonServices.class);
    private final PersonRepository personRepository;

    public PersonServices(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findById(Long id) {
        logger.info("Procurando uma pessoa");
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID"));

    }

    public Person create(Person person) {
        logger.info("Criando uma pessoa");
        return personRepository.save(person);

    }

    public Person update(Person person) {
        logger.info("Atualizando uma pessoa");
       var entity = personRepository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

       return personRepository.save(entity);

    }

    public void delete(Long id) {
        logger.info("deletando uma pessoa");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID"));

        personRepository.delete(entity);
    }


    public List<Person> findAll() {
        logger.info("Listando pessoas");
        return personRepository.findAll();

    }



}
