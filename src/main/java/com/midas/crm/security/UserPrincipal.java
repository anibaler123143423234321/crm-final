package com.midas.crm.security;

import com.midas.crm.entity.User;
import com.midas.crm.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private User user;           // Solo se usa al iniciar sesión como usuario
    private Set<GrantedAuthority> authorities = new HashSet<>(); // Initialize with empty Set

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    // Constructor que toma un User como parámetro
    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.user = user;
        this.authorities = Collections.singleton(SecurityUtils.convertToAuthority(user.getRole().name()));
    }



    public static UserPrincipal build(User user) {
        return new UserPrincipal(user);
    }
}