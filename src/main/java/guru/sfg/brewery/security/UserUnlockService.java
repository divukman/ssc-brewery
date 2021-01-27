package guru.sfg.brewery.security;


import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserUnlockService {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 5000)
    public void unlockAccounts() {
        log.debug("Running unlock accounts...");
        final List<User> users =  userRepository.findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
                Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

        if (!users.isEmpty()) {
            log.debug("Found locked accounts...");
            users.forEach(user -> user.setAccountNonLocked(false));

            userRepository.saveAll(users);
        }
    }
}
