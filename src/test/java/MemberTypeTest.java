
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberTypeTest {

    @ParameterizedTest
    @CsvSource({
            "STUDENT, 2, 14",
            "STAFF, 5, 30"
    })
    void shouldReturnCorrectLimits(
            MemberType type,
            int expectedMaxLoans,
            int expectedLoanDays) {

        assertEquals(expectedMaxLoans, type.maxLoans());
        assertEquals(expectedLoanDays, type.loanDays());
    }
}
