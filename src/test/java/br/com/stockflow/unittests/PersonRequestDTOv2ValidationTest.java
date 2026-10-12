package br.com.stockflow.unittests;

import br.com.stockflow.dto.v2.PersonRequestDTOv2;
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

@DisplayName("PersonRequestDTOv2 validation (v1 rules + required, well-formed email)")
class PersonRequestDTOv2ValidationTest {

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

    private static PersonRequestDTOv2 withEmail(String email) {
        return new PersonRequestDTOv2("Ana", "Lima", "Rua A, 10", email, "F");
    }

    private static Set<String> invalidFields(PersonRequestDTOv2 request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("a complete request is valid")
    void completeRequestIsValid() {
        assertThat(validator.validate(withEmail("ana@email.com"))).isEmpty();
    }

    @Test
    @DisplayName("an empty request reports all five fields")
    void emptyRequestReportsAllFields() {
        var request = new PersonRequestDTOv2(null, null, null, null, null);

        assertThat(invalidFields(request))
                .containsExactlyInAnyOrder("firstName", "lastName", "address", "email", "gender");
    }

    @Nested
    @DisplayName("email")
    class EmailField {

        @ParameterizedTest(name = "email = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("is required")
        void required(String value) {
            assertThat(invalidFields(withEmail(value))).containsExactly("email");
        }

        @ParameterizedTest(name = "email = \"{0}\"")
        @ValueSource(strings = {"abc", "ana@", "@email.com", "ana email@x.com", "ana@@email.com"})
        @DisplayName("rejects malformed addresses")
        void rejectsMalformed(String value) {
            assertThat(invalidFields(withEmail(value))).containsExactly("email");
        }

        @ParameterizedTest(name = "email = \"{0}\"")
        @ValueSource(strings = {"ana@email.com", "ana.lima@empresa.com.br", "ana+estoque@x.io"})
        @DisplayName("accepts well-formed addresses")
        void acceptsWellFormed(String value) {
            assertThat(validator.validate(withEmail(value))).isEmpty();
        }

        @Test
        @DisplayName("accepts exactly 80 characters")
        void acceptsMaxLength() {
            // the part before @ may have at most 64 characters (RFC 5321), so the domain fills the rest
            var email = "a".repeat(64) + "@" + "b".repeat(11) + ".com"; // 80 characters

            assertThat(validator.validate(withEmail(email))).isEmpty();
        }

        @Test
        @DisplayName("rejects more than 80 characters")
        void rejectsTooLong() {
            var email = "a".repeat(64) + "@" + "b".repeat(12) + ".com"; // 81 characters

            assertThat(invalidFields(withEmail(email))).containsExactly("email");
        }
    }

    @Nested
    @DisplayName("fields shared with v1 keep the same rules")
    class SharedRules {

        @Test
        @DisplayName("blank names and address are rejected")
        void blankFieldsRejected() {
            var request = new PersonRequestDTOv2(" ", " ", " ", "ana@email.com", "F");

            assertThat(invalidFields(request))
                    .containsExactlyInAnyOrder("firstName", "lastName", "address");
        }

        @Test
        @DisplayName("column limits are enforced")
        void limitsEnforced() {
            var request = new PersonRequestDTOv2(
                    "a".repeat(81), "b".repeat(81), "c".repeat(151), "ana@email.com", "FF");

            assertThat(invalidFields(request))
                    .containsExactlyInAnyOrder("firstName", "lastName", "address", "gender");
        }
    }
}
