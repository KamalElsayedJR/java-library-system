import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    void shouldCreateMemberSuccessfully() {
        Member member = new Member(1, "Kamal", MemberType.STUDENT);

        assertEquals(1, member.id());
        assertEquals("Kamal", member.name());
        assertEquals(MemberType.STUDENT, member.type());
    }

    @Test
    void shouldCompareMembersByTheirValues() {
        Member first = new Member(1, "Kamal", MemberType.STUDENT);
        Member second = new Member(1, "Kamal", MemberType.STUDENT);

        assertEquals(first, second);
    }

    @Test
    void shouldReturnFalseWhenMembersHaveDifferentValues() {
        Member first = new Member(1, "Kamal", MemberType.STUDENT);
        Member second = new Member(2, "Ahmed", MemberType.STAFF);

        assertNotEquals(first, second);
    }
}
