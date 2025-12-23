package com.ngv.libraryManagementSystem.entity;

import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "members")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @Column(nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatusEnum status = MemberStatusEnum.ACTIVE;

    @OneToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToMany(mappedBy = "member")
    private List<LoanEntity> loans;

    @OneToMany(mappedBy = "member")
    private List<ReservationEntity> reservations;

    @OneToMany(mappedBy = "member")
    private List<FineEntity> fines;

    @OneToOne(mappedBy = "member")
    private UserEntity user;
}
