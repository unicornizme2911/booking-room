package com.booking.models;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Data
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String gender;

    private String contact;

    private String email;

    private String password;

    private String address;

    private String avatar;

    private Date date_created;

    private Date date_updated;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToMany(mappedBy = "users",targetEntity = TokenModel.class)
    @JsonIgnore
    private Collection<TokenModel> tokens;

    @OneToMany(mappedBy = "user",targetEntity = ReservationModel.class)
    private Collection<ReservationModel> reservations;

    @OneToMany(mappedBy = "user",targetEntity = FeedbackModel.class)
    private Collection<FeedbackModel> feedbacks;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));

    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}