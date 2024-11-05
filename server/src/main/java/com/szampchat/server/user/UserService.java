package com.szampchat.server.user;

import com.szampchat.server.upload.FileNotFoundException;
import com.szampchat.server.upload.FilePath;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.user.dto.request.UserCreateRequest;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.entity.UserSubject;
import com.szampchat.server.user.exception.UserAlreadyExistsException;
import com.szampchat.server.user.exception.UserNotFoundException;
import com.szampchat.server.user.exception.UsernameAlreadyExistsException;
import com.szampchat.server.user.repository.UserRepository;
import com.szampchat.server.user.repository.UserSubjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.FileSystemException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserSubjectRepository userSubjectRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    @Deprecated
    public Mono<User> findUser(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    public Mono<UserDTO> findUserDTO(Long userId) {
        return findUser(userId)
            .map(user -> modelMapper.map(user, UserDTO.class));
    }

    public Flux<UserDTO> findUsers(List<Long> userIds) {
        return userRepository.findByIdIn(userIds)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    //easy to cache, will practically never change
    public Mono<Long> findUserIdBySub(UUID sub) {
        //TODO Throw 404
        return userSubjectRepository.findBySub(sub)
                .map(UserSubject::getUserId);
    }

    public Mono<User> findUserBySub(UUID sub) {
        //TODO Throw 404
        return findUserIdBySub(sub)
                .flatMap(userRepository::findById);
    }

    public Mono<UserDTO> createUser(UserCreateRequest userCreateRequest, UUID currentUserId) {
        //If user doesn't exist, create user
        return findUserIdBySub(currentUserId)
                .flatMap(_ -> Mono.error(new UserAlreadyExistsException()))
                //Save user to database
                .switchIfEmpty(
                        userRepository.findByUsername(userCreateRequest.getUsername())
                                .flatMap(_ -> Mono.error(new UsernameAlreadyExistsException(userCreateRequest.getUsername())))
                        .map(userDto -> modelMapper.map(userDto, User.class))
                        .flatMap(userRepository::save)
                        //We have to wait for userRepository::save to finish to obtain id of user in database
                        //Saving user id <-> keycloak id mapping
                        .flatMap(savedUser -> userSubjectRepository.save(
                                UserSubject.builder()
                                        .userId(savedUser.getId())
                                        .sub(currentUserId)
                                        .build())
                                .then(Mono.just(savedUser)))
                )
                //Map saved user to UserDTO
                .map(savedUser -> modelMapper.map(savedUser, UserDTO.class));

    }

    public Mono<UserDTO> editAvatar(FilePart file, Long userId){
        return userRepository.findById(userId)
            .flatMap(user -> {
                if(user.getImageUrl() != null){
                    try {
                        fileStorageService.delete(user.getImageUrl());
                    } catch (Exception e) {
                        return Mono.error(new FileNotFoundException("Error during deleting file: " + e.getMessage()));
                    }
                }

                return fileStorageService.save(file, FilePath.AVATAR)
                    .flatMap(filepath -> {
                        user.setImageUrl(filepath);
                        return userRepository.save(user)
                            .map(updatedUser -> modelMapper.map(updatedUser, UserDTO.class));
                    });
            });
    }

    public Mono<UserDTO> editDescription(String description, Long id){
        return Mono.empty();
    }

    public Mono<Void> deleteUser(Long id){
        return userRepository.findById(id)
            .flatMap(existingCommunity -> {
                if (existingCommunity.getImageUrl() != null) {
                    try {
                        return fileStorageService.delete(existingCommunity.getImageUrl())
                                .then(userRepository.deleteById(id));
                    } catch (FileSystemException e) {
                        return Mono.error(e.getCause());
                    }
                } else {
                    return userRepository.deleteById(id);
                }
            });
    }
}
