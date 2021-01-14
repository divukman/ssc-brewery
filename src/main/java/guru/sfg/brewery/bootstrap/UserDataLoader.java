package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadAuthoritiesAndUsers();
    }

    private void loadAuthoritiesAndUsers() {
        if (authorityRepository.findAll().isEmpty()) {
            // Beer Auths
            final Authority createBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.create").build());
            final Authority readBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.read").build());
            final Authority updateBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.update").build());
            final Authority deleteBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.delete").build());

            // Customer Auths
            final Authority createCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.create").build());
            final Authority readCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.read").build());
            final Authority updateCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.update").build());
            final Authority deleteCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.delete").build());

            // Brewery Auths
            final Authority createBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.create").build());
            final Authority readBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.read").build());
            final Authority updateBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.update").build());
            final Authority deleteBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.delete").build());

            // Roles
            final Role roleAdmin = roleRepository.save(Role.builder().name("ADMIN").build());
            final Role roleCustomer = roleRepository.save(Role.builder().name("CUSTOMER").build());
            final Role roleUser = roleRepository.save(Role.builder().name("USER").build());

            roleAdmin.setAuthorities(
                    new HashSet<>(
                            Set.of(
                                createBeerAuthority, updateBeerAuthority, readBeerAuthority, deleteBeerAuthority,
                                createCustomerAuthority, readCustomerAuthority, updateCustomerAuthority, deleteCustomerAuthority,
                                createBreweryAuthority, readBreweryAuthority, updateBreweryAuthority, deleteBreweryAuthority)
                    )
            );
            roleCustomer.setAuthorities(
                    new HashSet<>(
                       Set.of(readBeerAuthority, readCustomerAuthority, readBreweryAuthority)
                    )
            );
            roleUser.setAuthorities(
                    new HashSet<>(
                        Set.of(readBeerAuthority)
                    )
            );

            roleRepository.saveAll(Arrays.asList(roleAdmin, roleCustomer, roleUser));


            final User springUser = User.builder()
                    .username("spring")
                    .password(passwordEncoder.encode("guru"))
                    .role(roleUser)
                    .build();

            final User regularUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("password"))
                    .role(roleUser)
                    .build();

            final User scottUser = User.builder()
                    .username("scott")
                    .password(passwordEncoder.encode("tiger"))
                    .role(roleCustomer)
                    .build();

            userRepository.save(springUser);
            userRepository.save(regularUser);
            userRepository.save(scottUser);
        }
    }
}
