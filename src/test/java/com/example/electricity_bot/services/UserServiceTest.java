package com.example.electricity_bot.services;

import com.example.electricity_bot.dto.UpdateUserProfileRequest;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {

    private static final String GREEN_COLOR = "\033[32m";
    private static final String RED_COLOR = "\033[31m";
    private static final String END_COLOR = "\033[0m";


    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks UserService userService;

    @Test
    void  shouldReturnEmptyIfEmailAlreadyExist() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        Optional<String> result = userService.register(user);
        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());

        System.out.println(GREEN_COLOR + "Test 'shouldReturnEmptyIfEmailAlreadyExist' PASSED" + END_COLOR);
    }

    @Test
    void shouldRegisterUserAndReturnToken() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("plaintext123");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("plaintext123");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plaintext123")).thenReturn(hashed);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        Optional<String> result = userService.register(user);
        assertTrue(result.isPresent());
        assertTrue(result.get().equals("fake-jwt-token"));

        verify(userRepository).save(any());
        verify(jwtService).generateToken(any());
        System.out.println(GREEN_COLOR + "Test 'shouldRegisterNewUserAndReturnToken' PASSED" + END_COLOR);
    }

   @Test
   void shouldLoginSuccessfullyAndReturnToken(){
        User user = new User();
        String email = "test@email.com";
        String password = "testPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(password);

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password,encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");

       Optional<String> result = userService.login(email, password);
       assertTrue(result.isPresent());
       assertTrue(result.get().equals("jwt-token"));
       verify(jwtService).generateToken(mockUser);
       System.out.println(GREEN_COLOR + "Test 'shouldLoginSuccessfullyAndReturnToken' PASSED" + END_COLOR);
   }


   @Test
    void shouldFailLoginWithWrongPassword(){
       String email = "test@email.com";
       String password = "testPassword";
       String encodedPassword = new BCryptPasswordEncoder().encode("correctPassword");

       User mockUser = new User();
       mockUser.setEmail(email);
       mockUser.setPassword(encodedPassword);

       when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
       when(passwordEncoder.matches(password,encodedPassword)).thenReturn(false);

    Optional<String> result = userService.login(email,password);
    assertTrue(result.isEmpty());
    verify(jwtService,never()).generateToken(any());
    System.out.println(GREEN_COLOR + "Test 'shouldFailLoginWithWrongPassword' PASSED" + END_COLOR);

   }

   @Test
    void shouldReturnEmptyIfEmailNotFound(){
        String email = " nonwxistwnd@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<String> result = userService.login(email,"somepassword");
        assertTrue(result.isEmpty());
        verify(jwtService,never()).generateToken(any());
       System.out.println(GREEN_COLOR + "Test 'shouldReturnEmptyIfEmailNotFound' PASSED" + END_COLOR);

   }


    @Test
    void testGetUserInfo_UserExists() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserInfo(email);
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }


    @Test
    void testGetUserInfo_UserDoesNotExist() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserInfo(email);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateAvatar_SuccessfulUpload() throws IOException {
        String email = "user@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setAvatar("old-avatar.jpg");

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        doNothing().when(file).transferTo(any(File.class));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = userService.updateAvatar(email, file);

        assertTrue(result.isPresent());
        assertEquals("user-avatar-1.png", result.get().getAvatar());
        verify(file).transferTo(any(File.class));
    }


    @Test
    void testUpdateAvatar_UserNotFound() throws Exception {
        String email = "missing@example.com";
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateAvatar(email, file);

        assertFalse(result.isPresent());
        verify(file, never()).transferTo(any(File.class));
    }


    @Test
    void testUpdateUserProfile_SuccessfulUpdate() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGender("male");
        request.setTimeZone("Europe/Kyiv");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = userService.updateUserProfile(email, request);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals("male", result.get().getGender());
        assertEquals("Europe/Kyiv", result.get().getTimezone());
    }


    @Test
    void testUpdateUserProfile_UserNotFound() {
        String email = "notfound@example.com";

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setGender("female");
        request.setTimeZone("Europe/London");
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUserProfile(email, request);
        assertFalse(result.isPresent());
    }

}
