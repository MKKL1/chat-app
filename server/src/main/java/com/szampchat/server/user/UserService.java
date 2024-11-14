package com.szampchat.server.user;

import com.szampchat.server.upload.FilePathType;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.upload.exception.FileNotProvidedException;
import com.szampchat.server.user.dto.request.UserCreateRequest;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.entity.UserSubject;
import com.szampchat.server.user.exception.KeycloakUserAlreadyExistsException;
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

    public Mono<UserDTO> findUserDTO(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    public Flux<UserDTO> findUsers(List<Long> userIds) {
        return userRepository.findByIdIn(userIds)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    //easy to cache, will practically never change
    public Mono<Long> findUserIdBySub(UUID sub) {
        return userSubjectRepository.findBySub(sub)
                .map(UserSubject::getUserId);
    }

    public Mono<User> findUserBySub(UUID sub) {
        return findUserIdBySub(sub)
                .flatMap(userRepository::findById);
    }

    public Mono<UserDTO> createUser(UserCreateRequest userCreateRequest, UUID currentUserId) {
        return checkUserExistsBySub(currentUserId)
                .flatMap(userExists -> {
                    if(userExists)
                        return Mono.error(new KeycloakUserAlreadyExistsException(currentUserId));
                    return Mono.empty();
                })
                //Mono defer is used here to not create Mono response immediately (wait for code above to fail)
                .then(Mono.defer(() -> checkUsernameExists(userCreateRequest.getUsername())))
                .flatMap(usernameExists -> {
                    if(usernameExists)
                        return Mono.error(new UsernameAlreadyExistsException(userCreateRequest.getUsername()));
                    return Mono.just(true);
                })
                .then(saveNewUser(userCreateRequest, currentUserId));
    }

    private Mono<Boolean> checkUserExistsBySub(UUID sub) {
        return findUserBySub(sub).hasElement();
    }

    private Mono<Boolean> checkUsernameExists(String username) {
        return userRepository.findByUsername(username)
                .hasElement();
    }

    private Mono<UserDTO> saveNewUser(UserCreateRequest userCreateRequest, UUID currentUserId) {
        return Mono.just(userCreateRequest)
                .map(request -> modelMapper.map(request, User.class))
                .flatMap(userRepository::save)
                .flatMap(savedUser -> saveUserSubject(savedUser, currentUserId)
                        .thenReturn(savedUser)
                )
                .map(savedUser -> modelMapper.map(savedUser, UserDTO.class));
    }

    private Mono<UserSubject> saveUserSubject(User savedUser, UUID currentUserId) {
        UserSubject userSubject = UserSubject.builder()
                .userId(savedUser.getId())
                .sub(currentUserId)
                .build();

        return userSubjectRepository.save(userSubject);
    }


    public Mono<UserDTO> editAvatar(FilePart file, Long userId){
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> Mono.justOrEmpty(user.getImageUrl())
                        .flatMap(imageUrl -> fileStorageService.replace(file, FilePathType.AVATAR, imageUrl))
                        .switchIfEmpty(fileStorageService.upload(file, FilePathType.AVATAR))
                        .doOnNext(newFileDTO -> user.setImageUrl(newFileDTO.getId()))
                        .then(userRepository.save(user))
                ).map(this::toDTO);
    }

    public Mono<UserDTO> editDescription(String description, Long id){
        return Mono.empty();
    }

    public Mono<Void> deleteUser(Long id){
        return findUserDTO(id)
                .flatMap(user -> Mono.justOrEmpty(user.getImageUrl())
                        .flatMap(fileStorageService::delete)
                        .then(userRepository.deleteById(id))
                );
    }

    private UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
