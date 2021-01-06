package guru.sfg.brewery.temp;

import org.junit.Test;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncodersTest {

    @Test
    public void ldapTest() {
        final String password = "tiger";

        final PasswordEncoder passwordEncoder = new LdapShaPasswordEncoder();
        System.out.println("password => " + passwordEncoder.encode(password));
    }
}
