package gabia.cronMonitoring.entity;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Delegate
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }

    @Override
    public String getUsername() {
        return user.getAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
