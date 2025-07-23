package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import org.athletes.traineatrepeat.model.request.RegisterRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AccountService accountService;

  private RegisterRequest validRegisterRequestMale;
  private RegisterRequest validRegisterRequestFemale;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    validRegisterRequestMale =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "password123",
            "password123",
            30,
            "male",
            70.0f,
            175.0f);

    validRegisterRequestFemale =
        new RegisterRequest(
            "testuser2",
            "test2@example.com",
            "PassworD123!",
            "PassworD123!",
            25,
            "female",
            70.0f,
            175.0f);

    bindingResult = new BeanPropertyBindingResult(validRegisterRequestFemale, "registerRequest");
  }

  @Test
  @DisplayName("Should register a new user successfully")
  void registerUser_Success() {
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password()))
        .thenReturn("encodedPassword");

    accountService.registerUser(validRegisterRequestFemale, bindingResult);

    assertFalse(bindingResult.hasErrors(), "BindingResult should not have errors");
    verify(userRepository).findByEmail(validRegisterRequestFemale.email());
    verify(passwordEncoder).encode(validRegisterRequestFemale.password());
    verify(userRepository).save(any(UserDTO.class));
  }

  @Test
  @DisplayName("Should not register user with existing email")
  void registerUser_EmailAlreadyExists() {
    UserDTO existingUser = new UserDTO();
    existingUser.setEmail(validRegisterRequestFemale.email());
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(existingUser);

    accountService.registerUser(validRegisterRequestFemale, bindingResult);

    assertTrue(bindingResult.hasErrors(), "BindingResult should have errors");
    assertEquals(1, bindingResult.getErrorCount());
    FieldError emailError = bindingResult.getFieldError("email");
    assertNotNull(emailError);
    assertEquals("Email address is already used", emailError.getDefaultMessage());

    verify(userRepository, times(1)).findByEmail(validRegisterRequestFemale.email());
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder);
  }

  @Test
  @DisplayName(
      "Should calculate BMI and BMR correctly for both male and female users during registration")
  void registerUser_CalculatesBmiAndBmrCorrectlyForBothGenders() {
    when(userRepository.findByEmail(validRegisterRequestMale.email())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestMale.password()))
        .thenReturn("encodedPasswordMale");

    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password()))
        .thenReturn("encodedPasswordFemale");

    accountService.registerUser(validRegisterRequestMale, bindingResult);
    accountService.registerUser(validRegisterRequestFemale, bindingResult);

    ArgumentCaptor<UserDTO> userCaptor = ArgumentCaptor.forClass(UserDTO.class);
    verify(userRepository, times(2)).save(userCaptor.capture());
    List<UserDTO> savedUsers = userCaptor.getAllValues();

    UserDTO savedMaleUser =
        savedUsers.stream()
            .filter(u -> u.getEmail().equals(validRegisterRequestMale.email()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Male user not found in saved list"));

    UserDTO savedFemaleUser =
        savedUsers.stream()
            .filter(u -> u.getEmail().equals(validRegisterRequestFemale.email()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Female user not found in saved list"));

    float expectedBmiMale =
        validRegisterRequestMale.weight()
            / (float) Math.pow(validRegisterRequestMale.height() / 100.0, 2);
    assertEquals(expectedBmiMale, savedMaleUser.getBmi(), 0.01, "BMI should be correct for male");

    float expectedBmrMale =
        (10 * validRegisterRequestMale.weight())
            + (6.25f * validRegisterRequestMale.height())
            - (5 * validRegisterRequestMale.age())
            + 5;
    assertEquals(expectedBmrMale, savedMaleUser.getBmr(), 0.1, "BMR should be correct for male");

    float expectedBmiFemale =
        validRegisterRequestFemale.weight()
            / (float) Math.pow(validRegisterRequestFemale.height() / 100.0, 2);
    assertEquals(
        expectedBmiFemale, savedFemaleUser.getBmi(), 0.01, "BMI should be correct for female");

    float expectedBmrFemale =
        (10 * validRegisterRequestFemale.weight())
            + (6.25f * validRegisterRequestFemale.height())
            - (5 * validRegisterRequestFemale.age())
            - 161;
    assertEquals(
        expectedBmrFemale, savedFemaleUser.getBmr(), 0.1, "BMR should be correct for female");
  }

  @Test
  @DisplayName("Should save encoded password")
  void registerUser_SavesEncodedPassword() {
    when(userRepository.findByEmail(any())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password())).thenReturn("myEncodedPass");

    accountService.registerUser(validRegisterRequestFemale, bindingResult);

    ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass(UserDTO.class);
    verify(userRepository).save(captor.capture());
    assertEquals("myEncodedPass", captor.getValue().getPassword());
  }

  @Test
  @DisplayName("Should propagate exception when save fails")
  void registerUser_SaveFails() {
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password()))
        .thenReturn("encodedPassword");

    doThrow(new RuntimeException("Database error")).when(userRepository).save(any(UserDTO.class));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              accountService.registerUser(validRegisterRequestFemale, bindingResult);
            });
    assertEquals("Database error", exception.getMessage(), "Exception message should match");

    verify(userRepository).findByEmail(validRegisterRequestFemale.email());
    verify(passwordEncoder).encode(validRegisterRequestFemale.password());
    verify(userRepository).save(any(UserDTO.class));

    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(passwordEncoder);
  }

  @Test
  @DisplayName("Should not save user if BindingResult has errors before processing")
  void registerUser_BindingResultHasErrors() {
    bindingResult.addError(new FieldError("registerRequest", "username", "Username is required"));

    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);
    accountService.registerUser(validRegisterRequestFemale, bindingResult);

    verify(userRepository).findByEmail(validRegisterRequestFemale.email());
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder);

    assertTrue(bindingResult.hasErrors(), "BindingResult should still have errors");
  }
}
