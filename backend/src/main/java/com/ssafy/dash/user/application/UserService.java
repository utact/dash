package com.ssafy.dash.user.application;

import com.ssafy.dash.user.application.dto.command.UserCreateCommand;
import com.ssafy.dash.user.application.dto.command.UserUpdateCommand;
import com.ssafy.dash.user.application.dto.result.UserResult;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.domain.UserRepository;
import com.ssafy.dash.user.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResult create(UserCreateCommand command) {
        User u = User.create(command.username(), command.email(), LocalDateTime.now(), null, null, null);
        userRepository.save(u);

        return UserResult.from(u);
    }

    @Transactional(readOnly = true)
    public UserResult findById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserResult.from(u);
    }

    @Transactional(readOnly = true)
    public List<UserResult> findAll() {

        return userRepository.findAll().stream()
                .map(UserResult::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResult update(Long id, UserUpdateCommand command) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        u.updateProfile(command.username(), command.email(), u.getAvatarUrl());
        userRepository.update(u);

        return UserResult.from(u);
    }

    @Transactional
    public void delete(Long id) {
        boolean deleted = userRepository.delete(id);
        if (!deleted) throw new UserNotFoundException(id);
    }

}
