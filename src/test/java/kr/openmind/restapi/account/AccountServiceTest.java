package kr.openmind.restapi.account;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService sut;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void loadUserByUsername_not_exists_email_then_throw_exception() {
        String notExistsEmail = "notExistsEmail@email.com";

        given(accountRepository.findByEmailIgnoreCase(anyString())).willReturn(Optional.empty());

        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Not found email address: " + notExistsEmail);

        sut.loadUserByUsername(notExistsEmail);
    }

    @Test
    public void loadUserByUsername_exists_email_then_userDetails() {
        String anyExistsEmail = "anyExistsEmail@email.com";

        Account account = new Account();
        account.setEmail(anyExistsEmail);
        account.setPassword("anyPassword");
        account.setRoles(Sets.newSet(AccountRole.USER, AccountRole.ADMIN));

        given(accountRepository.findByEmailIgnoreCase(anyString())).willReturn(Optional.of(account));

        UserDetails actual = sut.loadUserByUsername(anyExistsEmail);

        assertThat(actual.getUsername()).isEqualTo(anyExistsEmail);
        assertThat(actual.getPassword()).isEqualTo(account.getPassword());
        assertThat(actual.getAuthorities()).hasSize(2);
        assertThat(actual.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
            .containsExactly("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    public void createAccount_password_then_encode() {
        Account account = new Account();
        account.setEmail("anyEmail@email.com");
        account.setPassword("anyPassword");
        account.setRoles(Sets.newSet(AccountRole.USER, AccountRole.ADMIN));

        given(passwordEncoder.encode(anyString())).willReturn("encodePassword");
        given(accountRepository.save(account)).willReturn(account);

        Account actual = sut.createAccount(account);

        assertThat(actual.getPassword()).isEqualTo("encodePassword");
    }
}