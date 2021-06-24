package gabia.cronMonitoring.entity;

import gabia.cronMonitoring.entity.Enum.UserRole;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "account", unique = true)
    @NotNull
    @NonNull
    private String account;

    @Column(name = "name")
    @NotNull
    @NonNull
    private String name;

    @Column(name = "email")
    @NotNull
    @NonNull
    private String email;

    @Column(name = "password")
    @NotNull
    @NonNull
    private String password;

    @Column(name = "role")
    @NotNull
    @NonNull
    @Enumerated(EnumType.STRING)
    private UserRole role;


}
