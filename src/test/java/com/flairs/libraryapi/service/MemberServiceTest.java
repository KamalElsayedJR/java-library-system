package com.flairs.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flairs.libraryapi.dto.CreateMemberRequest;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.model.Role;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import com.flairs.libraryapi.security.CurrentMember;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    static final CurrentMember SARA = new CurrentMember(1L, "sara@example.com", Role.MEMBER);
    static final CurrentMember LIBRARIAN = new CurrentMember(3L, "librarian@example.com", Role.LIBRARIAN);

    @Mock MemberRepository members;
    @Mock LoanRepository loans;
    final PasswordEncoder passwords = new BCryptPasswordEncoder(4);   // low cost = fast tests

    MemberService service;

    @BeforeEach
    void setUp() {
        service = new MemberService(members, loans, passwords);
    }

    @Test
    void aMemberSeesTheirOwnLoans() {
        when(members.existsById(1L)).thenReturn(true);
        when(loans.findByMemberIdWithDetails(1L)).thenReturn(List.of());

        assertThat(service.loansOf(1L, SARA)).isEmpty();
    }

    @Test
    void anotherMembersLoansAreNotFound() {
        assertThatThrownBy(() -> service.loansOf(2L, SARA)).isInstanceOf(NotFoundException.class);
        verify(loans, never()).findByMemberIdWithDetails(any());
    }

    @Test
    void aLibrarianSeesAnyMember() {
        Member omar = new Member("Omar", "omar@example.com", "hash");
        ReflectionTestUtils.setField(omar, "id", 2L);
        when(members.findById(2L)).thenReturn(Optional.of(omar));

        assertThat(service.findById(2L, LIBRARIAN).name()).isEqualTo("Omar");
    }

    @Test
    void createStoresAHashNotThePassword() {
        when(members.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateMemberRequest("Nour", "Nour@Example.com", "password123"));

        verify(members).save(org.mockito.ArgumentMatchers.argThat(m ->
                m.getEmail().equals("nour@example.com")
                        && m.getRole() == Role.MEMBER
                        && !m.getPasswordHash().equals("password123")
                        && passwords.matches("password123", m.getPasswordHash())));
    }
}
