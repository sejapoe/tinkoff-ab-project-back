package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "privilege")
public class PrivilegeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privilege_seq")
    @SequenceGenerator(name = "privilege_seq", sequenceName = "privilege_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private List<RoleEntity> roles;
}
