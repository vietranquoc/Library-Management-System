package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.FineEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<FineEntity, Long> {
    
    List<FineEntity> findByMember(MemberEntity member);
    
    List<FineEntity> findByMemberAndPaidFalse(MemberEntity member);
    
    Optional<FineEntity> findByLoan(LoanEntity loan);
}

