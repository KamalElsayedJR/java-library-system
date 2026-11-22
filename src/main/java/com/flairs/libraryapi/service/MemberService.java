package com.flairs.libraryapi.service;

import com.flairs.libraryapi.dto.CreateMemberRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.dto.MemberResponse;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import com.flairs.libraryapi.security.CurrentMember;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository members;
    private final LoanRepository loans;
    private final PasswordEncoder passwords;

    public MemberService(MemberRepository members, LoanRepository loans, PasswordEncoder passwords) {
        this.members = members;
        this.loans = loans;
        this.passwords = passwords;
    }

    public List<MemberResponse> findAll() {
        return members.findAll().stream().map(Mapper::toResponse).toList();
    }

    // A member may only see themselves. For anyone else we answer 404, not 403:
    // 403 would tell a stranger "this member exists".
    public MemberResponse findById(Long id, CurrentMember caller) {
        if (!caller.canSee(id)) {
            throw new NotFoundException("Member", id);
        }
        return Mapper.toResponse(members.findById(id).orElseThrow(() -> new NotFoundException("Member", id)));
    }

    public List<LoanResponse> loansOf(Long memberId, CurrentMember caller) {
        if (!caller.canSee(memberId) || !members.existsById(memberId)) {
            throw new NotFoundException("Member", memberId);
        }
        return loans.findByMemberIdWithDetails(memberId).stream().map(Mapper::toResponse).toList();
    }

    @Transactional
    public MemberResponse create(CreateMemberRequest request) {
        String email = request.email().trim().toLowerCase();
        if (members.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email " + email + " is already registered");
        }
        Member member = new Member(request.name().trim(), email, passwords.encode(request.password()));
        return Mapper.toResponse(members.save(member));
    }
}
