package guru.sfg.brewery.temp;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class EncodersTest {

    @Test
    public void ldapTest() {
        final String password = "tiger";

        final PasswordEncoder passwordEncoder = new LdapShaPasswordEncoder();
        System.out.println(password + " => " + passwordEncoder.encode(password));
    }


    @Test
    public void bcryptTest() {
        final String password = "guru";
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(password + " => " + passwordEncoder.encode(password));
    }

    @Test
    public void bcrypt10Test() {
        final String password = "tiger";
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        System.out.println(password + " => " + passwordEncoder.encode(password));
    }

    @Test
    public void sha256Test() {
        final String password = "password";
        final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
        System.out.println(password + " => " + passwordEncoder.encode(password));
    }
}
