package com.flairs.libraryapi.security;

import com.flairs.libraryapi.model.Role;
import org.springframework.security.oauth2.jwt.Jwt;

// Who is calling, read from the verified JWT. Services take it as a plain parameter,
// so the rules ("only your own loans") are normal Java and easy to unit test.
public record CurrentMember(Long id, String email, Role role) {

    public static CurrentMember from(Jwt jwt) {
        return new CurrentMember(Long.valueOf(jwt.getSubject()), jwt.getClaimAsString("email"),
                Role.valueOf(jwt.getClaimAsStringList("roles").getFirst()));
    }

    public boolean isLibrarian() {
        return role == Role.LIBRARIAN;
    }

    // May this caller see data that belongs to the member with this id?
    // TODO 4: a librarian sees everyone, a member only themselves
    public boolean canSee(Long memberId) {
        return isLibrarian() || memberId.equals(id);
    }
}
