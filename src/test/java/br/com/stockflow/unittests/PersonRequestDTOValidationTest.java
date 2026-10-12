package br.com.stockflow.unittests;

import br.com.stockflow.dto.v1.PersonRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PersonRequestDTO validation (mirrors the person table)")
class PersonRequestDTOValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private static PersonRequestDTO valid() {
        return new PersonRequestDTO("Ana", "Lima", "Rua A, 10", "F");
    }

    private static Set<String> invalidFields(PersonRequestDTO request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("a complete request is valid")
    void completeRequestIsValid() {
        assertThat(validator.validate(valid())).isEmpty();
    }

    @Test
    @DisplayName("values exactly at the column limits are valid")
    void valuesAtLimitsAreValid() {
        var request = new PersonRequestDTO("a".repeat(80), "b".repeat(80), "c".repeat(150), "M");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    @DisplayName("an empty request reports all four fields")
    void emptyRequestReportsAllFields() {
        var request = new PersonRequestDTO(null, null, null, null);

        assertThat(invalidFields(request))
                .containsExactlyInAnyOrder("firstName", "lastName", "address", "gender");
    }

    @Nested
    @DisplayName("required fields (NOT NULL in the table)")
    class Required {

        @ParameterizedTest(name = "firstName = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("firstName cannot be null or blank")
        void firstNameRequired(String value) {
            var request = new PersonRequestDTO(value, "Lima", "Rua A, 10", "F");

            assertThat(invalidFields(request)).containsExactly("firstName");
        }

        @ParameterizedTest(name = "lastName = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("lastName cannot be null or blank")
        void lastNameRequired(String value) {
            var request = new PersonRequestDTO("Ana", value, "Rua A, 10", "F");

            assertThat(invalidFields(request)).containsExactly("lastName");
        }

        @ParameterizedTest(name = "address = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("address cannot be null or blank")
        void addressRequired(String value) {
            var request = new PersonRequestDTO("Ana", "Lima", value, "F");

            assertThat(invalidFields(request)).containsExactly("address");
        }

        @ParameterizedTest(name = "gender = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("gender cannot be null or blank")
        void genderRequired(String value) {
            var request = new PersonRequestDTO("Ana", "Lima", "Rua A, 10", value);

            assertThat(invalidFields(request)).containsExactly("gender");
        }
    }

    @Nested
    @DisplayName("max length (column size in the table)")
    class MaxLength {

        @Test
        @DisplayName("firstName over 80 characters is rejected")
        void firstNameTooLong() {
            var request = new PersonRequestDTO("a".repeat(81), "Lima", "Rua A, 10", "F");

            assertThat(invalidFields(request)).containsExactly("firstName");
        }

        @Test
        @DisplayName("lastName over 80 characters is rejected")
        void lastNameTooLong() {
            var request = new PersonRequestDTO("Ana", "b".repeat(81), "Rua A, 10", "F");

            assertThat(invalidFields(request)).containsExactly("lastName");
        }

        @Test
        @DisplayName("address over 150 characters is rejected")
        void addressTooLong() {
            var request = new PersonRequestDTO("Ana", "Lima", "c".repeat(151), "F");

            assertThat(invalidFields(request)).containsExactly("address");
        }

        @ParameterizedTest(name = "gender = \"{0}\"")
        @ValueSource(strings = {"FF", "Feminino", "Masculino"})
        @DisplayName("gender longer than 1 character is rejected")
        void genderTooLong(String value) {
            var request = new PersonRequestDTO("Ana", "Lima", "Rua A, 10", value);

            assertThat(invalidFields(request)).containsExactly("gender");
        }
    }
}
