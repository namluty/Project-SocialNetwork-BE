package com.meta.socialnetwork.service.user;

import com.meta.socialnetwork.model.User;
import com.meta.socialnetwork.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService implements IUserService {

    @Autowired
    IUserRepository userRepository;


    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Iterable<User> findByFullNameIsContaining(String fullName) {
        return userRepository.findByFullNameIsContaining(fullName);
    }

    @Override
    public List<User> finAllByRoleName(String roleName) {
        List<User> user = new ArrayList<>();
        List<User> userList = (List<User>)findAll();
        for (User u: userList
        ) {
            u.getRoles().forEach(role -> {
                if (role.getName().name().equals(roleName)){
                    user.add(u);
                }
            });
        }
        return user;
    }

    @Override
    public Iterable<User> findAllByUsernameIsContaining(String username) {
        return userRepository.findAllByUsernameIsContaining(username);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Iterable<User> findAllByOrderByIdDesc() {
        return userRepository.findAllByOrderByIdDesc();
    }

}
