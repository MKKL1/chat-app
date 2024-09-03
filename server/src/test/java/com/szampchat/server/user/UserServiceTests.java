package com.szampchat.server.user;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.snowflake.Snowflake;
import com.szampchat.server.user.dto.UserCreateDTO;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.entity.UserSubject;
import com.szampchat.server.user.exception.UserAlreadyExistsException;
import com.szampchat.server.user.repository.UserRepository;
import com.szampchat.server.user.repository.UserSubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSubjectRepository userSubjectRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    @Spy
    private UserService userService;

    @Test
    public void findUserIdBySub_SubExists_ReturnsUserId() {
        // Arrange
        UUID sub = UUID.randomUUID();
        Long expectedUserId = 123L;
        UserSubject userSubject = new UserSubject();
        userSubject.setUserId(expectedUserId);

        doReturn(Mono.just(userSubject)).when(userSubjectRepository).findBySub(sub);

        // Act
        Mono<Long> result = userService.findUserIdBySub(sub);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedUserId)
                .verifyComplete();
    }

    @Test
    public void findUserIdBySub_SubDoesNotExist_ReturnsEmpty() {
        // Arrange
        UUID sub = UUID.randomUUID();

        doReturn(Mono.empty()).when(userSubjectRepository).findBySub(sub);

        // Act
        Mono<Long> result = userService.findUserIdBySub(sub);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }


    @Test
    public void createUser_UserAlreadyExists_ReturnsError() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        UUID currentUserSub = UUID.randomUUID();
        Long currentUserId = 123L;

        doReturn(Mono.just(currentUserId)).when(userService).findUserIdBySub(currentUserSub);

        // Act
        Mono<UserDTO> result = userService.createUser(userCreateDTO, currentUserSub);

        // Assert
        StepVerifier.create(result)
                .expectError(UserAlreadyExistsException.class)
                .verify();
    }

    @Test
    public void findUserBySub_SubExists_ReturnsUser() {
        // Arrange
        UUID sub = UUID.randomUUID();
        Long userId = 123L;
        User expectedUser = new User();
        expectedUser.setId(userId);

        doReturn(Mono.just(userId)).when(userService).findUserIdBySub(sub);
        doReturn(Mono.just(expectedUser)).when(userRepository).findById(userId);

        // Act
        Mono<User> result = userService.findUserBySub(sub);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedUser)
                .verifyComplete();
    }

    @Test
    public void findUserBySub_SubDoesNotExist_ReturnsEmpty() {
        // Arrange
        UUID sub = UUID.randomUUID();

        doReturn(Mono.empty()).when(userService).findUserIdBySub(sub);

        // Act
        Mono<User> result = userService.findUserBySub(sub);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();  // Expects no value and completes successfully
    }

    @Test
    public void findUserBySub_UserIdExistsButUserNotFound_ReturnsEmpty() {
        // Arrange
        UUID sub = UUID.randomUUID();
        Long userId = 123L;

        doReturn(Mono.just(userId)).when(userService).findUserIdBySub(sub);
        doReturn(Mono.empty()).when(userRepository).findById(userId);

        // Act
        Mono<User> result = userService.findUserBySub(sub);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();  // Expects no value and completes successfully
    }

    @Test
    public void createUser_UserDoesNotExist_CreateUserAndReturnUserDTO() {
        UserCreateDTO userCreateDTO = new UserCreateDTO(); // Populate with test data
        UUID currentUserSub = UUID.randomUUID();

        User user = new User(); // Create a User entity for the mapping
        user.setId(123L);

        UserDTO userDTO = new UserDTO(); // Create a UserDTO for the mapping

        doReturn(Mono.empty()).when(userService).findUserIdBySub(currentUserSub);

        when(modelMapper.map(userCreateDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(userSubjectRepository.save(any(UserSubject.class)))
                .thenReturn(Mono.just(new UserSubject()));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        // Act
        Mono<UserDTO> result = userService.createUser(userCreateDTO, currentUserSub);

        // Assert
        StepVerifier.create(result)
                .expectNext(userDTO)
                .verifyComplete();

        // Verify that the save methods were called with the correct parameters
        verify(userRepository).save(user);
        verify(userSubjectRepository).save(any(UserSubject.class));
        verify(modelMapper).map(userCreateDTO, User.class);
        verify(modelMapper).map(user, UserDTO.class);
    }


}
