package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.ChangePasswordRequest;
import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.entity.User;
import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.mapper.UserMapper;
import com.xdpsx.music.repository.UserRepository;
import com.xdpsx.music.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void changePassword(ChangePasswordRequest request, User loggedUser) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password are not the same");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), loggedUser.getPassword())) {
            throw new BadRequestException("Wrong password");
        }
        loggedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(loggedUser);
    }

    @Override
    public UserProfileResponse getUserProfile(User loggedUser) {
        return userMapper.fromEntityToProfileResponse(loggedUser);
    }
}
