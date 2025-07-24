package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock private JavaMailSender mailSender;

  @Mock private MimeMessage mimeMessage;

  @InjectMocks private EmailService emailService;

  @Test
  @DisplayName("Send verification email - successfully sends email")
  void sendVerificationEmail_Success() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    String to = "user@example.com";
    String username = "testuser";
    String code = "123456";

    emailService.sendVerificationEmail(to, username, code);

    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessage);
  }

  @Test
  @DisplayName("Send verification email - throws MailException on failure")
  void sendVerificationEmail_ThrowsMailException() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new MailSendException("Test exception")).when(mailSender).send(any(MimeMessage.class));

    String to = "user@example.com";
    String username = "testuser";
    String code = "123456";

    assertThrows(MailException.class, () -> emailService.sendVerificationEmail(to, username, code));
  }

  @Test
  @DisplayName("Send password reset email - successfully sends email")
  void sendPasswordResetEmail_Success() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    String to = "user@example.com";
    String username = "testuser";
    String token = "reset-token-123";

    emailService.sendPasswordResetEmail(to, username, token);

    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessage);
  }

  @Test
  @DisplayName("Send password reset email - throws MailException on failure")
  void sendPasswordResetEmail_ThrowsMailException() {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new MailSendException("Test exception")).when(mailSender).send(any(MimeMessage.class));

    String to = "user@example.com";
    String username = "testuser";
    String token = "reset-token-123";

    assertThrows(
        MailException.class, () -> emailService.sendPasswordResetEmail(to, username, token));
  }

  @Test
  @DisplayName("Send verification email - uses correct recipient and subject")
  void sendVerificationEmail_CorrectRecipientAndSubject() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    String to = "user@example.com";
    String username = "testuser";
    String code = "654321";

    emailService.sendVerificationEmail(to, username, code);

    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

    verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), addressCaptor.capture());
    verify(mimeMessage).setSubject(subjectCaptor.capture());

    assertInstanceOf(InternetAddress.class, addressCaptor.getValue());
    assertEquals(to, ((InternetAddress) addressCaptor.getValue()).getAddress());
    assertEquals("Email Verification", subjectCaptor.getValue());
  }

  @Test
  @DisplayName("Send password reset email - uses correct recipient and subject")
  void sendPasswordResetEmail_CorrectRecipientAndSubject() throws Exception {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    String to = "reset@example.com";
    String username = "reset_user";
    String token = "token-987654";

    emailService.sendPasswordResetEmail(to, username, token);

    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

    verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), addressCaptor.capture());
    verify(mimeMessage).setSubject(subjectCaptor.capture());

    assertInstanceOf(InternetAddress.class, addressCaptor.getValue());
    assertEquals(to, ((InternetAddress) addressCaptor.getValue()).getAddress());
    assertEquals("Password Reset Request", subjectCaptor.getValue());
  }
}
