package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private EmailService emailService;
  @Mock private Model model;

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
            "PassworD123!",
            "PassworD123!",
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
            60.0f,
            170.0f);

    bindingResult = new BeanPropertyBindingResult(validRegisterRequestFemale, "registerRequest");
  }

  private void testValidationError(RegisterRequest request, String field, String expectedMessage)
      throws Exception {
    BindingResult testBindingResult = new BeanPropertyBindingResult(request, "registerRequest");
    lenient().when(userRepository.findByEmail(anyString())).thenReturn(null);

    String viewName = accountService.registerUser(request, testBindingResult, model);

    assertTrue(testBindingResult.hasErrors());
    assertEquals("register", viewName);
    FieldError error = testBindingResult.getFieldError(field);
    assertNotNull(error);
    assertEquals(expectedMessage, error.getDefaultMessage());
    verify(userRepository, never()).save(any(UserDTO.class));
    verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("Should register a new user and send a verification email successfully")
  void registerUser_Success() throws Exception {
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password()))
        .thenReturn("encodedPassword");

    String viewName = accountService.registerUser(validRegisterRequestFemale, bindingResult, model);

    assertFalse(bindingResult.hasErrors(), "BindingResult should not have errors");
    assertEquals("redirect:/verify-email?email=test2@example.com", viewName);
    verify(userRepository).findByEmail(validRegisterRequestFemale.email());
    verify(passwordEncoder).encode(validRegisterRequestFemale.password());
    verify(userRepository).save(any(UserDTO.class));
    verify(emailService)
        .sendVerificationEmail(
            eq(validRegisterRequestFemale.email()),
            eq(validRegisterRequestFemale.username()),
            anyString());
    verifyNoInteractions(model);
  }

  @Test
  @DisplayName("Should not register user with existing email and return 'register' view")
  void registerUser_EmailAlreadyExists() {
    UserDTO existingUser = new UserDTO();
    existingUser.setEmail(validRegisterRequestFemale.email());
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(existingUser);

    String viewName = accountService.registerUser(validRegisterRequestFemale, bindingResult, model);

    assertTrue(bindingResult.hasErrors(), "BindingResult should have errors");
    assertEquals("register", viewName);
    assertEquals(1, bindingResult.getErrorCount());
    FieldError emailError = bindingResult.getFieldError("email");
    assertNotNull(emailError);
    assertEquals("Email address is already used", emailError.getDefaultMessage());

    verify(userRepository, times(1)).findByEmail(validRegisterRequestFemale.email());
    verify(model).addAttribute(eq("registerRequest"), any(RegisterRequest.class));
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(emailService);
  }

  @Test
  @DisplayName("Should calculate BMI and BMR correctly for both genders and save user")
  void registerUser_CalculatesBmiAndBmrCorrectlyForBothGenders() throws Exception {
    when(userRepository.findByEmail(anyString())).thenReturn(null);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    BindingResult maleBindingResult =
        new BeanPropertyBindingResult(validRegisterRequestMale, "registerRequest");
    String maleViewName =
        accountService.registerUser(validRegisterRequestMale, maleBindingResult, model);
    assertFalse(maleBindingResult.hasErrors());
    assertEquals("redirect:/verify-email?email=test@example.com", maleViewName);

    BindingResult femaleBindingResult =
        new BeanPropertyBindingResult(validRegisterRequestFemale, "registerRequest");
    String femaleViewName =
        accountService.registerUser(validRegisterRequestFemale, femaleBindingResult, model);
    assertFalse(femaleBindingResult.hasErrors());
    assertEquals("redirect:/verify-email?email=test2@example.com", femaleViewName);

    ArgumentCaptor<UserDTO> userCaptor = ArgumentCaptor.forClass(UserDTO.class);
    verify(userRepository, times(2)).save(userCaptor.capture());
    List<UserDTO> savedUsers = userCaptor.getAllValues();

    UserDTO savedMaleUser =
        savedUsers.stream()
            .filter(u -> "test@example.com".equals(u.getEmail()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Male user not found in saved list"));

    UserDTO savedFemaleUser =
        savedUsers.stream()
            .filter(u -> "test2@example.com".equals(u.getEmail()))
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

    verify(emailService, times(2)).sendVerificationEmail(anyString(), anyString(), anyString());
    verifyNoInteractions(model);
  }

  @Test
  @DisplayName("Should save encoded password, verification code, and expiration time")
  void registerUser_SavesEncodedPasswordAndVerificationDetails() {
    when(userRepository.findByEmail(any())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password())).thenReturn("myEncodedPass");

    accountService.registerUser(validRegisterRequestFemale, bindingResult, model);

    ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass(UserDTO.class);
    verify(userRepository).save(captor.capture());
    UserDTO savedUser = captor.getValue();
    assertEquals("myEncodedPass", savedUser.getPassword());
    assertNotNull(savedUser.getVerificationCode());
    assertFalse(savedUser.isEmailVerified());
    assertNotNull(savedUser.getVerificationExpiresAt());
    assertTrue(savedUser.getVerificationExpiresAt().isAfter(LocalDateTime.now()));
  }

  @Test
  @DisplayName("Should handle email service failure gracefully")
  void registerUser_EmailServiceFails() throws Exception {
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);
    when(passwordEncoder.encode(validRegisterRequestFemale.password()))
        .thenReturn("encodedPassword");
    doThrow(new RuntimeException("Email sending failed"))
        .when(emailService)
        .sendVerificationEmail(anyString(), anyString(), anyString());

    String viewName = accountService.registerUser(validRegisterRequestFemale, bindingResult, model);

    assertEquals("register", viewName);
    verify(userRepository).save(any(UserDTO.class));
    verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
    verify(model).addAttribute("error", "Failed to send verification email. Please try again.");
    verify(model).addAttribute(eq("registerRequest"), any(RegisterRequest.class));
  }

  @Test
  @DisplayName("Should not save user if BindingResult has errors before processing")
  void registerUser_BindingResultHasErrors() {
    bindingResult.addError(new FieldError("registerRequest", "username", "Username is required"));
    when(userRepository.findByEmail(validRegisterRequestFemale.email())).thenReturn(null);

    String viewName = accountService.registerUser(validRegisterRequestFemale, bindingResult, model);

    verify(userRepository).findByEmail(validRegisterRequestFemale.email());
    verify(model).addAttribute(eq("registerRequest"), any(RegisterRequest.class));
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(emailService);

    assertTrue(bindingResult.hasErrors(), "BindingResult should still have errors");
    assertEquals("register", viewName);
  }

  @Test
  @DisplayName("Should not register user if username is invalid and return 'register' view")
  void registerUser_InvalidUsername_ReturnsRegisterView() {
    RegisterRequest invalidRequest =
        new RegisterRequest(
            "", "test@example.com", "PassworD123!", "PassworD123!", 25, "female", 60.0f, 170.0f);

    BindingResult invalidBindingResult =
        new BeanPropertyBindingResult(invalidRequest, "registerRequest");
    when(userRepository.findByEmail(invalidRequest.email())).thenReturn(null);

    String viewName = accountService.registerUser(invalidRequest, invalidBindingResult, model);

    assertTrue(invalidBindingResult.hasErrors());
    assertEquals("register", viewName);
    FieldError usernameError = invalidBindingResult.getFieldError("username");
    assertNotNull(usernameError);
    assertEquals("Username is required", usernameError.getDefaultMessage());
    verify(model).addAttribute("registerRequest", invalidRequest);

    verify(userRepository, times(1)).findByEmail(invalidRequest.email());
    verify(userRepository, never()).save(any(UserDTO.class));

    verifyNoInteractions(emailService);
    verifyNoInteractions(passwordEncoder);
  }

  @Test
  @DisplayName("Should return 'login' view if email is already verified")
  void verifyEmail_EmailAlreadyVerified_RedirectsToLogin() {
    String email = "verified@example.com";
    String code = "valid-code";
    UserDTO verifiedUser = new UserDTO();
    verifiedUser.setEmail(email);
    verifiedUser.setVerificationCode(code);
    verifiedUser.setVerificationExpiresAt(LocalDateTime.now().plusHours(1));
    verifiedUser.setEmailVerified(true);
    when(userRepository.findByEmail(email)).thenReturn(verifiedUser);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("redirect:/login", viewName);
    verify(userRepository).findByEmail(email);

    verifyNoInteractions(passwordEncoder, emailService);

    verify(model, never()).addAttribute(eq("error"), anyString());

    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("Should return 'verify-email' view with error if verification code is invalid")
  void verifyEmail_InvalidCode_ReturnsWithError() {
    String email = "test@example.com";
    String validCode = "valid-code";
    String invalidCode = "invalid-code";
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setVerificationCode(validCode);
    user.setVerificationExpiresAt(LocalDateTime.now().plusHours(1));
    user.setEmailVerified(false);
    when(userRepository.findByEmail(email)).thenReturn(user);

    String viewName = accountService.verifyEmail(email, invalidCode, model);

    assertEquals("verify-email", viewName);
    verify(userRepository).findByEmail(email);
    verify(model).addAttribute("error", "Invalid or expired verification code");
    verify(model).addAttribute("email", email);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("Prepare registration form returns register view")
  void prepareRegistrationForm_whenCalled_returnsRegisterView() {
    String viewName = accountService.prepareRegistrationForm(model);

    assertEquals("register", viewName);
    verify(model).addAttribute(eq("registerRequest"), any(RegisterRequest.class));
    verifyNoMoreInteractions(model);
  }

  @Test
  @DisplayName("Prepare verification page with error for empty email")
  void prepareVerificationPage_whenEmailIsEmpty_addsErrorToModel() {
    String email = "";

    when(model.containsAttribute("error")).thenReturn(true);

    String viewName = accountService.prepareVerificationPage(email, model);

    assertEquals("register", viewName);
    verify(model).addAttribute("error", "Email is required");
    verifyNoMoreInteractions(model);
  }

  @Test
  @DisplayName("Prepare verification page with error for invalid email format")
  void prepareVerificationPage_whenEmailIsInvalid_addsFormatErrorToModel() {
    String email = "invalid-email";

    var testModel = new ExtendedModelMap();

    String viewName = accountService.prepareVerificationPage(email, testModel);

    assertEquals("register", viewName);
    assertTrue(testModel.containsAttribute("error"));
    assertEquals("Invalid email format", testModel.getAttribute("error"));
  }

  @Test
  @DisplayName("Prepare verification page success for valid email")
  void prepareVerificationPage_whenEmailIsValid_returnsVerificationView() {
    String email = "user@example.com";

    when(model.containsAttribute("error")).thenReturn(false);

    String viewName = accountService.prepareVerificationPage(email, model);

    assertEquals("verify-email", viewName);
    verify(model).addAttribute("email", email);
    verifyNoMoreInteractions(model);
  }

  @Test
  @DisplayName("Verify email returns verify email if user not found")
  void verifyEmail_whenUserNotFound_returnsVerifyEmailView() {
    String email = "notfound@example.com";
    String code = "valid-code";

    when(userRepository.findByEmail(email)).thenReturn(null);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("verify-email", viewName);

    verify(userRepository).findByEmail(email);
    verify(model).addAttribute("error", "No user found with this email");
    verify(model).addAttribute("email", email);

    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(passwordEncoder, emailService);
  }

  @Test
  @DisplayName("Verify email returns verify email if verification code is null")
  void verifyEmail_whenVerificationCodeIsNull_returnsVerifyEmailView() {
    String email = "user@example.com";
    String code = null;
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setVerificationCode("valid-code");
    user.setVerificationExpiresAt(LocalDateTime.now().plusHours(1));
    when(userRepository.findByEmail(email)).thenReturn(user);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("verify-email", viewName);
    verify(userRepository).findByEmail(email);
    verify(model).addAttribute("error", "Invalid or expired verification code");
    verify(model).addAttribute("email", email);
  }

  @Test
  @DisplayName("Verify email returns verify email if verification code does not match")
  void verifyEmail_whenVerificationCodeDoesNotMatch_returnsVerifyEmailView() {
    String email = "user@example.com";
    String validCode = "valid-code";
    String invalidCode = "invalid-code";
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setVerificationCode(validCode);
    user.setVerificationExpiresAt(LocalDateTime.now().plusHours(1));
    user.setEmailVerified(false);
    when(userRepository.findByEmail(email)).thenReturn(user);

    String viewName = accountService.verifyEmail(email, invalidCode, model);

    assertEquals("verify-email", viewName);
    verify(userRepository).findByEmail(email);
    verify(model).addAttribute("error", "Invalid or expired verification code");
    verify(model).addAttribute("email", email);
  }

  @Test
  @DisplayName("Verify email returns verify email if verification expires at is null")
  void verifyEmail_whenExpiresAtIsNull_returnsVerifyEmailView() {
    String email = "user@example.com";
    String code = "code123";
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setVerificationCode(code);
    user.setVerificationExpiresAt(null);
    user.setEmailVerified(false);
    when(userRepository.findByEmail(email)).thenReturn(user);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("verify-email", viewName);
    verify(userRepository).findByEmail(email);
    verify(model).addAttribute("error", "Invalid or expired verification code");
    verify(model).addAttribute("email", email);
  }

  @Test
  @DisplayName("Verify email returns verify email if verification code expired")
  void verifyEmail_whenCodeExpired_returnsVerifyEmailView() {
    String email = "user@example.com";
    String code = "code123";
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setVerificationCode(code);
    user.setVerificationExpiresAt(LocalDateTime.now().minusHours(1));
    user.setEmailVerified(false);
    when(userRepository.findByEmail(email)).thenReturn(user);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("verify-email", viewName);
    verify(userRepository).findByEmail(email);
    verify(model).addAttribute("error", "Invalid or expired verification code");
    verify(model).addAttribute("email", email);
  }

  @Test
  @DisplayName("Validate username adds error if empty")
  void validateUsername_whenEmpty_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "", "test@example.com", "PassworD123!", "PassworD123!", 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "username", "Username is required"));
  }

  @Test
  @DisplayName("Validate username adds error if too short")
  void validateUsername_whenTooShort_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "ab", "test@example.com", "PassworD123!", "PassworD123!", 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request, "username", "Username must be between 5 and 16 characters long"));
  }

  @Test
  @DisplayName("Validate username adds error if too long")
  void validateUsername_whenTooLong_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "thisisatoolongusername",
            "test@example.com",
            "PassworD123!",
            "PassworD123!",
            30,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request, "username", "Username must be between 5 and 16 characters long"));
  }

  @Test
  @DisplayName("Validate email adds error if empty")
  void validateEmail_whenEmpty_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser", "", "PassworD123!", "PassworD123!", 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "email", "Email is required"));
  }

  @Test
  @DisplayName("Validate email adds error if invalid format")
  void validateEmail_whenInvalidFormat_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser", "invalid-email", "PassworD123!", "PassworD123!", 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "email", "Invalid email format"));
  }

  @Test
  @DisplayName("Validate passwords adds error if empty")
  void validatePasswords_whenEmpty_addsError() {
    RegisterRequest request =
        new RegisterRequest("testuser", "test@example.com", "", "", 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "password", "Password is required"));
  }

  @Test
  @DisplayName("Validate passwords adds error if too short")
  void validatePasswords_whenTooShort_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser", "test@example.com", "Pass1!", "Pass1!", 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request, "password", "Password must be between 8 and 32 characters long"));
  }

  @Test
  @DisplayName("Validate passwords adds error if too long")
  void validatePasswords_whenTooLong_addsError() {
    String longPass =
        "PassworD123!PassworD123!PassworD123!PassworD123!PassworD123!PassworD123!PassworD123!PassworD123!PassworD123!PassworD123!";
    RegisterRequest request =
        new RegisterRequest(
            "testuser", "test@example.com", longPass, longPass, 30, "male", 70.0f, 175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request, "password", "Password must be between 8 and 32 characters long"));
  }

  @Test
  @DisplayName("Validate passwords adds error if no special character")
  void validatePasswords_whenMissingSpecialCharacter_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "Password123",
            "Password123",
            30,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request,
                "password",
                "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"));
  }

  @Test
  @DisplayName("Validate passwords adds error if no digit")
  void validatePasswords_whenMissingDigit_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "Password!@#",
            "Password!@#",
            30,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request,
                "password",
                "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"));
  }

  @Test
  @DisplayName("Validate passwords adds error if no lowercase")
  void validatePasswords_whenMissingLowercase_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "PASSWORD123!",
            "PASSWORD123!",
            30,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request,
                "password",
                "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"));
  }

  @Test
  @DisplayName("Validate passwords adds error if no uppercase")
  void validatePasswords_whenMissingUppercase_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "password123!",
            "password123!",
            30,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request,
                "password",
                "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"));
  }

  @Test
  @DisplayName("Validate passwords adds error if passwords do not match")
  void validatePasswords_whenPasswordsDoNotMatch_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "PassworD123!",
            "PassworD123!!",
            30,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(
        () ->
            testValidationError(
                request, "confirmPassword", "Password and Confirm Password do not match"));
  }

  @Test
  @DisplayName("Validate age adds error if null")
  void validateAge_whenNull_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "PassworD123!",
            "PassworD123!",
            null,
            "male",
            70.0f,
            175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "age", "Age is required"));
  }

  @Test
  @DisplayName("Should not register user if age is less than 1")
  void registerUser_InvalidAge_ReturnsRegisterView() {
    RegisterRequest underageRequest =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "PassworD123!",
            "PassworD123!",
            0,
            "female",
            55.0f,
            165.0f);

    BindingResult invalidBindingResult =
        new BeanPropertyBindingResult(underageRequest, "registerRequest");

    when(userRepository.findByEmail(underageRequest.email())).thenReturn(null);

    String viewName = accountService.registerUser(underageRequest, invalidBindingResult, model);

    assertEquals("register", viewName);
    assertTrue(invalidBindingResult.hasErrors());
    FieldError ageError = invalidBindingResult.getFieldError("age");
    assertNotNull(ageError);
    assertEquals("Age must be positive", ageError.getDefaultMessage());

    verify(model).addAttribute("registerRequest", underageRequest);
  }

  @Test
  @DisplayName("Validate weight adds error if less than one")
  void validateWeight_whenLessThanOne_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser",
            "test@example.com",
            "PassworD123!",
            "PassworD123!",
            30,
            "male",
            0.0f,
            175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "weight", "Weight must be positive"));
  }

  @Test
  @DisplayName("Validate gender adds error if empty")
  void validateGender_whenEmpty_addsError() {
    RegisterRequest request =
        new RegisterRequest(
            "testuser", "test@example.com", "PassworD123!", "PassworD123!", 30, "", 70.0f, 175.0f);
    assertDoesNotThrow(() -> testValidationError(request, "gender", "Gender is required"));
  }

  @Test
  @DisplayName("Should verify email successfully and redirect to login")
  void verifyEmail_SuccessfulVerification_RedirectsToLogin() {
    String email = "user@example.com";
    String code = "valid-code";

    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setVerificationCode(code);
    user.setVerificationExpiresAt(LocalDateTime.now().plusHours(1));
    user.setEmailVerified(false);

    when(userRepository.findByEmail(email)).thenReturn(user);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("redirect:/login", viewName);
    assertTrue(user.isEmailVerified());
    assertNull(user.getVerificationCode());
    assertNull(user.getVerificationExpiresAt());

    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should return verify-email view with email when error is present in model")
  void verifyEmail_ModelHasError_AddsEmailAndReturnsView() {
    String email = "user@example.com";
    String code = "any-code";

    when(userRepository.findByEmail(email)).thenReturn(null);

    String viewName = accountService.verifyEmail(email, code, model);

    assertEquals("verify-email", viewName);
    verify(model).addAttribute("email", email);
    verify(model).addAttribute(eq("error"), anyString());
  }
}
