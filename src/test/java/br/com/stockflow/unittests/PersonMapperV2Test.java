package br.com.stockflow.unittests;

import br.com.stockflow.dto.v1.PersonRequestDTO;
import br.com.stockflow.dto.v2.PersonRequestDTOv2;
import br.com.stockflow.dto.v2.PersonResponseDTOv2;
import br.com.stockflow.mapper.PersonMapper;
import br.com.stockflow.mapper.PersonMapperV2;
import br.com.stockflow.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PersonMapperV2")
class PersonMapperV2Test {

    private static PersonRequestDTOv2 mockRequest(int n) {
        return new PersonRequestDTOv2(
                "First " + n,
                "Last " + n,
                "Address " + n,
                "person" + n + "@email.com",
                n % 2 == 0 ? "M" : "F");
    }

    private static Person mockEntity(long id) {
        var person = new Person();
        person.setId(id);
        person.setFirstName("First " + id);
        person.setLastName("Last " + id);
        person.setAddress("Address " + id);
        person.setEmail("person" + id + "@email.com");
        person.setGender(id % 2 == 0 ? "M" : "F");
        return person;
    }

    @Nested
    @DisplayName("toEntity: PersonRequestDTOv2 -> new Person")
    class ToEntity {

        @Test
        @DisplayName("copies every field, including the email")
        void copiesAllFields() {
            var person = PersonMapperV2.toEntity(mockRequest(1));

            assertThat(person.getFirstName()).isEqualTo("First 1");
            assertThat(person.getLastName()).isEqualTo("Last 1");
            assertThat(person.getAddress()).isEqualTo("Address 1");
            assertThat(person.getEmail()).isEqualTo("person1@email.com");
            assertThat(person.getGender()).isEqualTo("F");
        }

        @Test
        @DisplayName("never sets the id")
        void leavesIdNull() {
            assertThat(PersonMapperV2.toEntity(mockRequest(1)).getId()).isNull();
        }

        @Test
        @DisplayName("throws NullPointerException for a null request")
        void rejectsNullRequest() {
            assertThatThrownBy(() -> PersonMapperV2.toEntity(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("copyToEntity: PersonRequestDTOv2 -> existing Person")
    class CopyToEntity {

        @Test
        @DisplayName("overwrites every field, including the email, and keeps the id")
        void overwritesFieldsAndKeepsId() {
            var entity = mockEntity(7);
            var request = new PersonRequestDTOv2("New", "Name", "New street", "new@email.com", "M");

            PersonMapperV2.copyToEntity(request, entity);

            assertThat(entity.getId()).isEqualTo(7L);
            assertThat(entity.getFirstName()).isEqualTo("New");
            assertThat(entity.getLastName()).isEqualTo("Name");
            assertThat(entity.getAddress()).isEqualTo("New street");
            assertThat(entity.getEmail()).isEqualTo("new@email.com");
            assertThat(entity.getGender()).isEqualTo("M");
        }
    }

    @Nested
    @DisplayName("toResponse: Person -> PersonResponseDTOv2")
    class ToResponse {

        @Test
        @DisplayName("copies every field in the right order (email is not swapped with gender)")
        void copiesAllFieldsInOrder() {
            var response = PersonMapperV2.toResponse(mockEntity(5));

            assertThat(response).isEqualTo(new PersonResponseDTOv2(
                    5L, "First 5", "Last 5", "Address 5", "person5@email.com", "F"));
            assertThat(response.email()).isEqualTo("person5@email.com");
            assertThat(response.gender()).isEqualTo("F");
        }

        @Test
        @DisplayName("returns a null email for people created through v1")
        void nullEmailForV1People() {
            var entity = mockEntity(5);
            entity.setEmail(null);

            assertThat(PersonMapperV2.toResponse(entity).email()).isNull();
        }

        @Test
        @DisplayName("maps a list keeping size and order")
        void mapsListInOrder() {
            var responses = IntStream.rangeClosed(1, 5)
                    .mapToObj(PersonMapperV2Test::mockEntity)
                    .map(PersonMapperV2::toResponse)
                    .toList();

            assertThat(responses)
                    .extracting(PersonResponseDTOv2::id)
                    .containsExactly(1L, 2L, 3L, 4L, 5L);
        }
    }

    @Nested
    @DisplayName("v1 and v2 living together")
    class Coexistence {

        @Test
        @DisplayName("a v1 update keeps the email set through v2")
        void v1UpdateKeepsEmail() {
            var entity = PersonMapperV2.toEntity(mockRequest(1)); // created through v2, with email
            var v1Request = new PersonRequestDTO("Changed", "By v1", "Other street", "M");

            PersonMapper.copyToEntity(v1Request, entity);

            assertThat(entity.getFirstName()).isEqualTo("Changed");
            assertThat(entity.getEmail()).isEqualTo("person1@email.com");
        }

        @Test
        @DisplayName("the v1 response never exposes the email")
        void v1ResponseHasNoEmail() {
            var v1Response = PersonMapper.toResponse(mockEntity(3));

            assertThat(v1Response.toString()).doesNotContain("person3@email.com");
        }
    }
}
