package com.xdpsx.music.security;

import com.xdpsx.music.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        if (!user.isEnabled()) {
//            throw new DisabledException(String.format("User with email=%s is not active", username));
//        }
//        if (user.isAccountLocked()) {
//            throw new LockedException(String.format("User with email=%s is locked", username));
//        }

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("User with email=%s not found", username)));
    }
}
