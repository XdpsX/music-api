package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.ChangePasswordRequest;
import com.xdpsx.music.dto.request.UserProfileRequest;
import com.xdpsx.music.dto.request.params.UserParams;
import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.dto.response.UserResponse;
import com.xdpsx.music.entity.User;
import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.UserMapper;
import com.xdpsx.music.repository.UserRepository;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.service.TokenService;
import com.xdpsx.music.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.xdpsx.music.constant.FileConstants.USERS_IMG_FOLDER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final TokenService tokenService;

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

    @Override
    public UserProfileResponse updateUserProfile(User loggedUser,
                                                 UserProfileRequest request, MultipartFile image) {
        boolean needUpdate = false;
        if (!loggedUser.getName().equals(request.getName())){
            loggedUser.setName(request.getName());
            needUpdate = true;
        }
        if (image != null){
            String imageUrl = fileService.uploadFile(image, USERS_IMG_FOLDER);
            loggedUser.setAvatar(imageUrl);
            needUpdate = true;
        }

        if (needUpdate){
            User savedUser = userRepository.save(loggedUser);
            return userMapper.fromEntityToProfileResponse(savedUser);
        }
        return userMapper.fromEntityToProfileResponse(loggedUser);
    }

    @Transactional
    @Override
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("User with id=%s not found", userId)));
        if (!user.isAccountLocked()){
            user.setAccountLocked(true);
            userRepository.save(user);
            tokenService.revokeAllJwtTokens(user);
        }
    }

    @Override
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("User with id=%s not found", userId)));
        if (user.isAccountLocked()){
            user.setAccountLocked(false);
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("User with id=%s not found", userId)));
        userRepository.delete(user);
        if (user.getAvatar() != null){
            fileService.deleteFileByUrl(user.getAvatar());
        }
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(UserParams params) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<User> userPage = userRepository.findAllWithFilters(
                pageable, params.getSearch(), params.getSort(), params.getAccountLocked(), params.getEnabled()
        );
        List<UserResponse> responses = userPage.getContent().stream()
                .map(userMapper::fromEntityToResponse)
                .collect(Collectors.toList());
        return PageResponse.<UserResponse>builder()
                .items(responses)
                .pageNum(userPage.getNumber() + 1)
                .pageSize(userPage.getSize())
                .totalItems(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }


}
