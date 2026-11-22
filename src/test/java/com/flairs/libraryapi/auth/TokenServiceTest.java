package com.flairs.libraryapi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.model.Role;
import com.flairs.libraryapi.security.CurrentMember;
import com.flairs.libraryapi.security.JwtConfig;
import com.flairs.libraryapi.security.JwtProperties;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

// The real encoder and decoder from JwtConfig. No Spring context, no database.
class TokenServiceTest {

    static final JwtProperties PROPS = new JwtProperties("test-secret-test-secret-test-secret-123", "library-api",
            Duration.ofMinutes(30));

    final JwtConfig config = new JwtConfig();
    final SecretKey key = config.jwtSecretKey(PROPS);
    final JwtDecoder decoder = config.jwtDecoder(key, PROPS);

    TokenService serviceAt(Instant now) {
        return new TokenService(config.jwtEncoder(key), PROPS, Clock.fixed(now, ZoneOffset.UTC));
    }

    Member librarian() {
        Member m = new Member("Mona", "librarian@example.com", "hash", Role.LIBRARIAN);
        ReflectionTestUtils.setField(m, "id", 3L);
        return m;
    }

    @Test
    void theTokenCarriesTheMemberIdAndRole() {
        TokenResponse tokens = serviceAt(Instant.now()).issue(librarian());

        Jwt jwt = decoder.decode(tokens.accessToken());
        CurrentMember caller = CurrentMember.from(jwt);
        assertThat(caller.id()).isEqualTo(3L);
        assertThat(caller.isLibrarian()).isTrue();
        assertThat(tokens.tokenType()).isEqualTo("Bearer");
        assertThat(tokens.expiresIn()).isEqualTo(1800);
    }

    @Test
    void anExpiredTokenIsRejected() {
        TokenResponse old = serviceAt(Instant.now().minus(Duration.ofHours(2))).issue(librarian());

        assertThatThrownBy(() -> decoder.decode(old.accessToken())).isInstanceOf(JwtException.class);
    }

    @Test
    void aChangedTokenIsRejected() {
        String token = serviceAt(Instant.now()).issue(librarian()).accessToken();
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1] + "x." + parts[2];

        assertThatThrownBy(() -> decoder.decode(tampered)).isInstanceOf(JwtException.class);
    }
}
