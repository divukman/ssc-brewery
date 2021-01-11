package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadAuthoritiesAndUsers();
    }

    private void loadAuthoritiesAndUsers() {
        if (authorityRepository.findAll().isEmpty()) {
            final Authority adminAuthority = Authority.builder().role("ADMIN").build();
            final Authority userAuthority = Authority.builder().role("USER").build();
            final Authority customerAuthority = Authority.builder().role("CUSTOMER").build();

            authorityRepository.save(adminAuthority);
            authorityRepository.save(userAuthority);
            authorityRepository.save(customerAuthority);

            final User springUser = User.builder()
                    .username("spring")
                    .password(passwordEncoder.encode("guru"))
                    .authority(adminAuthority)
                    .build();

            final User regularUser = User.builder()
                    .username("user")
                    .password("password")
                    .authority(userAuthority)
                    .build();

            final User scottUser = User.builder()
                    .username("scott")
                    .password("tiger")
                    .authority(customerAuthority)
                    .build();

            userRepository.save(springUser);
            userRepository.save(regularUser);
            userRepository.save(scottUser);
        }
    }
}
