package com.ngv.libraryManagementSystem.config;

import com.ngv.libraryManagementSystem.entity.ConfigEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.RoleEntity;
import com.ngv.libraryManagementSystem.entity.UserEntity;
import com.ngv.libraryManagementSystem.enums.ConfigDataTypeEnum;
import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import com.ngv.libraryManagementSystem.enums.RoleEnum;
import com.ngv.libraryManagementSystem.repository.ConfigRepository;
import com.ngv.libraryManagementSystem.service.config.ConfigKeys;
import com.ngv.libraryManagementSystem.repository.MemberRepository;
import com.ngv.libraryManagementSystem.repository.RoleRepository;
import com.ngv.libraryManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final ConfigRepository configRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminAccount();
        initializeStaffAccount();
        initializeSystemConfig();
    }

    private void initializeRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            RoleEntity existingRole = roleRepository.findByName(roleEnum.name());
            if (existingRole == null) {
                RoleEntity role = new RoleEntity();
                role.setName(roleEnum.name());
                roleRepository.save(role);
            }
        }
    }

    private void initializeAdminAccount() {
        if (!userRepository.existsByUsername("admin")) {
            RoleEntity adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN.name());
            
            MemberEntity adminMember = new MemberEntity();
            adminMember.setFirstName("Admin");
            adminMember.setLastName("System");
            adminMember.setEmail("admin@library.com");
            adminMember.setPhone("0000000000");
            adminMember.setJoinDate(LocalDate.now());
            adminMember.setStatus(MemberStatusEnum.ACTIVE);
            adminMember = memberRepository.save(adminMember);

            UserEntity adminUser = new UserEntity();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setMember(adminMember);
            adminUser.setRoles(Set.of(adminRole));
            userRepository.save(adminUser);
        }
    }

    private void initializeStaffAccount() {
        if (!userRepository.existsByUsername("staff")) {
            RoleEntity staffRole = roleRepository.findByName(RoleEnum.ROLE_STAFF.name());
            
            MemberEntity staffMember = new MemberEntity();
            staffMember.setFirstName("Staff");
            staffMember.setLastName("Library");
            staffMember.setEmail("staff@library.com");
            staffMember.setPhone("0000000001");
            staffMember.setJoinDate(LocalDate.now());
            staffMember.setStatus(MemberStatusEnum.ACTIVE);
            staffMember = memberRepository.save(staffMember);

            UserEntity staffUser = new UserEntity();
            staffUser.setUsername("staff");
            staffUser.setPassword(passwordEncoder.encode("staff123"));
            staffUser.setMember(staffMember);
            staffUser.setRoles(Set.of(staffRole));
            userRepository.save(staffUser);
        }
    }

    private void initializeSystemConfig() {
        // Khởi tạo loan.period.days
        if (!configRepository.existsByConfigKey(ConfigKeys.LOAN_PERIOD_DAYS)) {
            ConfigEntity config = new ConfigEntity();
            config.setConfigKey(ConfigKeys.LOAN_PERIOD_DAYS);
            config.setConfigValue("7");
            config.setDataType(ConfigDataTypeEnum.INTEGER);
            config.setDescription("Số ngày mượn sách mặc định");
            config.setConfigGroup("loan");
            configRepository.save(config);
        }

        // Khởi tạo fine.per.day
        if (!configRepository.existsByConfigKey(ConfigKeys.FINE_PER_DAY)) {
            ConfigEntity config = new ConfigEntity();
            config.setConfigKey(ConfigKeys.FINE_PER_DAY);
            config.setConfigValue("10000");
            config.setDataType(ConfigDataTypeEnum.DECIMAL);
            config.setDescription("Tiền phạt mỗi ngày quá hạn (VND)");
            config.setConfigGroup("fine");
            configRepository.save(config);
        }

        // Khởi tạo loan.max.books.per.member
        if (!configRepository.existsByConfigKey(ConfigKeys.MAX_BOOKS_PER_MEMBER)) {
            ConfigEntity config = new ConfigEntity();
            config.setConfigKey(ConfigKeys.MAX_BOOKS_PER_MEMBER);
            config.setConfigValue("5");
            config.setDataType(ConfigDataTypeEnum.INTEGER);
            config.setDescription("Số sách tối đa mà một member có thể mượn cùng lúc");
            config.setConfigGroup("loan");
            configRepository.save(config);
        }
    }
}

