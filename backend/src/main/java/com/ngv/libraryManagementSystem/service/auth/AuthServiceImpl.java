package com.ngv.libraryManagementSystem.service.auth;

import com.ngv.libraryManagementSystem.config.JwtService;
import com.ngv.libraryManagementSystem.dto.request.auth.LoginRequest;
import com.ngv.libraryManagementSystem.dto.request.auth.RegisterRequest;
import com.ngv.libraryManagementSystem.dto.response.auth.AuthResponse;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.RoleEntity;
import com.ngv.libraryManagementSystem.entity.UserEntity;
import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import com.ngv.libraryManagementSystem.enums.RoleEnum;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.exception.UnauthorizedException;
import com.ngv.libraryManagementSystem.repository.MemberRepository;
import com.ngv.libraryManagementSystem.repository.RoleRepository;
import com.ngv.libraryManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại đã được sử dụng");
        }

        MemberEntity member = new MemberEntity();
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setJoinDate(LocalDate.now());
        member.setStatus(MemberStatusEnum.ACTIVE);
        memberRepository.save(member);

        RoleEntity role = roleRepository.findByName(RoleEnum.ROLE_MEMBER.name());

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMember(member);
        user.setRoles(Set.of(role));
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        String token = jwtService.generateToken(claims, user.getUsername());

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        String token = jwtService.generateToken(claims, user.getUsername());
        return new AuthResponse(token);
    }
}
