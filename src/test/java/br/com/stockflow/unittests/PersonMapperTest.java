package br.com.stockflow.unittests;

import br.com.stockflow.dto.v1.PersonRequestDTO;
import br.com.stockflow.dto.v1.PersonResponseDTO;
import br.com.stockflow.mapper.PersonMapper;
import br.com.stockflow.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PersonMapper")
class PersonMapperTest {

    // ---------------------------------------------------------------
    // Test data builders
    // ---------------------------------------------------------------

    private static PersonRequestDTO mockRequest(int n) {
        return new PersonRequestDTO(
                "First " + n,
                "Last " + n,
                "Address " + n,
                n % 2 == 0 ? "M" : "F");
    }

    private static Person mockEntity(long id) {
        var person = new Person();
        person.setId(id);
        person.setFirstName("First " + id);
        person.setLastName("Last " + id);
        person.setAddress("Address " + id);
        person.setGender(id % 2 == 0 ? "M" : "F");
        return person;
    }

    // ---------------------------------------------------------------
    // DTO -> Entity (new)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("toEntity: PersonRequestDTO -> new Person")
    class ToEntity {

        @Test
        @DisplayName("copies every field from the request")
        void copiesAllFields() {
            var request = mockRequest(1);

            var person = PersonMapper.toEntity(request);

            assertThat(person.getFirstName()).isEqualTo("First 1");
            assertThat(person.getLastName()).isEqualTo("Last 1");
            assertThat(person.getAddress()).isEqualTo("Address 1");
            assertThat(person.getGender()).isEqualTo("F");
        }

        @Test
        @DisplayName("never sets the id (the database generates it)")
        void leavesIdNull() {
            var person = PersonMapper.toEntity(mockRequest(1));

            assertThat(person.getId()).isNull();
        }

        @Test
        @DisplayName("passes null fields through as null (validation is not the mapper's job)")
        void keepsNullFieldsNull() {
            var request = new PersonRequestDTO("Ana", "Lima", null, null);

            var person = PersonMapper.toEntity(request);

            assertThat(person.getFirstName()).isEqualTo("Ana");
            assertThat(person.getLastName()).isEqualTo("Lima");
            assertThat(person.getAddress()).isNull();
            assertThat(person.getGender()).isNull();
        }

        @Test
        @DisplayName("creates a new instance on every call")
        void returnsNewInstanceEachCall() {
            var request = mockRequest(1);

            var first = PersonMapper.toEntity(request);
            var second = PersonMapper.toEntity(request);

            assertThat(first).isNotSameAs(second);
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"José", "Conceição", "D'Ávila", "  spaced  ", "Ana-Maria", "名前"})
        @DisplayName("keeps special characters and whitespace untouched")
        void keepsTextUntouched(String name) {
            var request = new PersonRequestDTO(name, name, name, "F");

            var person = PersonMapper.toEntity(request);

            assertThat(person.getFirstName()).isEqualTo(name);
            assertThat(person.getLastName()).isEqualTo(name);
            assertThat(person.getAddress()).isEqualTo(name);
        }

        @Test
        @DisplayName("throws NullPointerException for a null request")
        void rejectsNullRequest() {
            assertThatThrownBy(() -> PersonMapper.toEntity(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ---------------------------------------------------------------
    // DTO -> Entity (existing, used by update)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("copyToEntity: PersonRequestDTO -> existing Person")
    class CopyToEntity {

        @Test
        @DisplayName("overwrites every field with the request values")
        void overwritesAllFields() {
            var entity = mockEntity(7);
            var request = new PersonRequestDTO("New first", "New last", "New address", "M");

            PersonMapper.copyToEntity(request, entity);

            assertThat(entity.getFirstName()).isEqualTo("New first");
            assertThat(entity.getLastName()).isEqualTo("New last");
            assertThat(entity.getAddress()).isEqualTo("New address");
            assertThat(entity.getGender()).isEqualTo("M");
        }

        @Test
        @DisplayName("preserves the id of the existing entity")
        void preservesId() {
            var entity = mockEntity(7);

            PersonMapper.copyToEntity(mockRequest(99), entity);

            assertThat(entity.getId()).isEqualTo(7L);
        }

        @Test
        @DisplayName("copies null fields too (PUT replaces everything)")
        void copiesNullFields() {
            var entity = mockEntity(7);
            var request = new PersonRequestDTO("Ana", "Lima", null, null);

            PersonMapper.copyToEntity(request, entity);

            assertThat(entity.getAddress()).isNull();
            assertThat(entity.getGender()).isNull();
        }

        @Test
        @DisplayName("does not change the request")
        void doesNotChangeRequest() {
            var request = mockRequest(3);
            var copy = mockRequest(3);

            PersonMapper.copyToEntity(request, mockEntity(7));

            assertThat(request).isEqualTo(copy);
        }

        @Test
        @DisplayName("throws NullPointerException for a null request")
        void rejectsNullRequest() {
            var entity = mockEntity(7);

            assertThatThrownBy(() -> PersonMapper.copyToEntity(null, entity))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("throws NullPointerException for a null entity")
        void rejectsNullEntity() {
            var request = mockRequest(1);

            assertThatThrownBy(() -> PersonMapper.copyToEntity(request, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ---------------------------------------------------------------
    // Entity -> DTO
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("toResponse: Person -> PersonResponseDTO")
    class ToResponse {

        @Test
        @DisplayName("copies every field, including the id")
        void copiesAllFieldsIncludingId() {
            var response = PersonMapper.toResponse(mockEntity(5));

            assertThat(response).isEqualTo(
                    new PersonResponseDTO(5L, "First 5", "Last 5", "Address 5", "F"));
        }

        @Test
        @DisplayName("keeps null fields null")
        void keepsNullFields() {
            var entity = new Person();
            entity.setId(1L);

            var response = PersonMapper.toResponse(entity);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.firstName()).isNull();
            assertThat(response.lastName()).isNull();
            assertThat(response.address()).isNull();
            assertThat(response.gender()).isNull();
        }

        @Test
        @DisplayName("returns a null id for an entity that was never saved")
        void nullIdForUnsavedEntity() {
            var entity = PersonMapper.toEntity(mockRequest(1));

            var response = PersonMapper.toResponse(entity);

            assertThat(response.id()).isNull();
        }

        @Test
        @DisplayName("does not change the entity")
        void doesNotChangeEntity() {
            var entity = mockEntity(5);

            PersonMapper.toResponse(entity);

            assertThat(entity).isEqualTo(mockEntity(5));
        }

        @Test
        @DisplayName("throws NullPointerException for a null entity")
        void rejectsNullEntity() {
            assertThatThrownBy(() -> PersonMapper.toResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ---------------------------------------------------------------
    // Full round trips and lists
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Round trips")
    class RoundTrips {

        @Test
        @DisplayName("create flow: request -> entity -> (saved) -> response keeps the data and gets the id")
        void createFlow() {
            var request = mockRequest(4);

            var entity = PersonMapper.toEntity(request);
            entity.setId(42L); // simulates the id generated by the database on save
            var response = PersonMapper.toResponse(entity);

            assertThat(response.id()).isEqualTo(42L);
            assertThat(response.firstName()).isEqualTo(request.firstName());
            assertThat(response.lastName()).isEqualTo(request.lastName());
            assertThat(response.address()).isEqualTo(request.address());
            assertThat(response.gender()).isEqualTo(request.gender());
        }

        @Test
        @DisplayName("update flow: existing entity + request -> response keeps the id with new data")
        void updateFlow() {
            var entity = mockEntity(10);
            var request = new PersonRequestDTO("Updated", "Person", "New street", "M");

            PersonMapper.copyToEntity(request, entity);
            var response = PersonMapper.toResponse(entity);

            assertThat(response).isEqualTo(
                    new PersonResponseDTO(10L, "Updated", "Person", "New street", "M"));
        }

        @Test
        @DisplayName("list of entities -> list of responses keeps size and order")
        void mapsListInOrder() {
            var entities = IntStream.rangeClosed(1, 14)
                    .mapToObj(PersonMapperTest::mockEntity)
                    .toList();

            var responses = entities.stream()
                    .map(PersonMapper::toResponse)
                    .toList();

            assertThat(responses).hasSize(14);
            assertThat(responses)
                    .extracting(PersonResponseDTO::id)
                    .containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L);
            assertThat(responses.get(0).firstName()).isEqualTo("First 1");
            assertThat(responses.get(13).firstName()).isEqualTo("First 14");
        }

        @Test
        @DisplayName("empty list -> empty list")
        void mapsEmptyList() {
            List<Person> entities = List.of();

            var responses = entities.stream()
                    .map(PersonMapper::toResponse)
                    .toList();

            assertThat(responses).isEmpty();
        }
    }
}
