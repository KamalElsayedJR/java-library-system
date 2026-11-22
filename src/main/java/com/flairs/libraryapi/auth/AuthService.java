package com.flairs.libraryapi.auth;

import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.dto.MemberResponse;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final MemberRepository members;
    private final PasswordEncoder passwords;
    private final TokenService tokens;

    public AuthService(MemberRepository members, PasswordEncoder passwords, TokenService tokens) {
        this.members = members;
        this.passwords = passwords;
        this.tokens = tokens;
    }

    public TokenResponse login(LoginRequest request) {
        Member member = members.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwords.matches(request.password(), member.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        log.info("Member {} logged in", member.getId());
        return tokens.issue(member);
    }

    public MemberResponse me(Long memberId) {
        return Mapper.toResponse(members.findById(memberId).orElseThrow(() -> new NotFoundException("Member", memberId)));
    }
}
