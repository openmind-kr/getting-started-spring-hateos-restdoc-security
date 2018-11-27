package kr.openmind.restapi.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

    private static final String GRANTED_AUTHORITY_PREFIX = "ROLE_";

    @Getter
    private Account account;

    public AccountAdapter(Account account) {
        super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> accountRoles) {
        return accountRoles.stream()
            .map(accountRole -> new SimpleGrantedAuthority(GRANTED_AUTHORITY_PREFIX.concat(accountRole.name())))
            .collect(Collectors.toSet());
    }
}
