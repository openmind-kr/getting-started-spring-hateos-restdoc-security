package kr.openmind.restapi.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void saveAndFind() {
        Account account = anAccount();

        Account savedAccount = accountRepository.save(account);

        Account actual = accountRepository.findById(savedAccount.getId()).get();

        assertThat(actual.getId()).isEqualTo(savedAccount.getId());
        assertThat(actual.getEmail()).isEqualTo(account.getEmail());
        assertThat(actual.getRoles()).isEqualTo(account.getRoles());
    }

    @Test
    public void findByEmailIgnoreCase() {
        Account account = anAccount();

        Account savedAccount = accountRepository.save(account);

        Optional<Account> accountByLowerCaseEmail = accountRepository.findByEmailIgnoreCase("master@openmind.kr");
        Optional<Account> accountByUpperCaseEmail = accountRepository.findByEmailIgnoreCase("MASTER@openmind.kr");

        assertThat(accountByLowerCaseEmail).hasValue(savedAccount);
        assertThat(accountByUpperCaseEmail).hasValue(savedAccount);
    }

    private Account anAccount() {
        Account account = new Account();
        account.setEmail("master@openmind.kr");
        account.setPassword("password");
        account.setRoles(new HashSet<>(Arrays.asList(AccountRole.values())));
        return account;
    }
}
