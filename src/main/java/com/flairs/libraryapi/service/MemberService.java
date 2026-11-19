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
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository members;
    private final LoanRepository loans;

    public MemberService(MemberRepository members, LoanRepository loans) {
        this.members = members;
        this.loans = loans;
    }

    public List<MemberResponse> findAll() {
        return members.findAll().stream().map(Mapper::toResponse).toList();
    }

    public MemberResponse findById(Long id) {
        return Mapper.toResponse(members.findById(id).orElseThrow(() -> new NotFoundException("Member", id)));
    }

    public List<LoanResponse> loansOf(Long memberId) {
        if (!members.existsById(memberId)) {
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
        return Mapper.toResponse(members.save(new Member(request.name().trim(), email)));
    }
}
