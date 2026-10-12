package br.com.stockflow.services;

import br.com.stockflow.dto.PersonRequestDTO;
import br.com.stockflow.dto.PersonResponseDTO;
import br.com.stockflow.exception.ResourceNotFoundException;
import br.com.stockflow.mapper.PersonMapper;
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

    private Person findEntity(Long id){
        return personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Person not found!"));
    }

    public PersonResponseDTO findById(Long id) {
        logger.info("Procurando a pessoa {}", id);
        return PersonMapper.toResponse(findEntity(id));

    }

    public List<PersonResponseDTO> findAll() {
        logger.info("Listando pessoas");
        return personRepository
                .findAll()
                .stream()
                .map(PersonMapper::toResponse)
                .toList();

    }


    public PersonResponseDTO create(PersonRequestDTO request) {
        logger.info("Criando uma pessoa");
        var savedPerson = personRepository.save(PersonMapper.toEntity(request));
        return PersonMapper.toResponse(savedPerson);

    }

    public PersonResponseDTO update(Long id, PersonRequestDTO request) {
        logger.info("Atualizando a pessoa {}", id);
       var entity = findEntity(id);
       PersonMapper.copyToEntity(request, entity);
       return PersonMapper.toResponse(personRepository.save(entity));
    }

    public void delete(Long id) {
        logger.info("Deletando a pessoa {}", id);
        personRepository.delete(findEntity(id));

    }




}
