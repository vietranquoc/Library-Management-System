package com.ngv.libraryManagementSystem.entity;

import com.ngv.libraryManagementSystem.enums.ConfigDataTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_config", uniqueConstraints = {
    @UniqueConstraint(columnNames = "config_key")
})
public class ConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_value", nullable = false, columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConfigDataTypeEnum dataType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "config_group", length = 50)
    private String configGroup;
}
