package guru.sfg.brewery.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
                http
                        .authorizeRequests( authorize -> authorize.antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll())
                        .authorizeRequests(
                                authorize -> authorize
                                        .antMatchers("/beers/find", "/beers*").permitAll()
                                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
                        )
                        .authorizeRequests().anyRequest().authenticated()
                        .and()
                        .formLogin().and()
                        .httpBasic();
    }


/*    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        final UserDetails admin = User.withDefaultPasswordEncoder()
                .username("spring")
                .password("guru")
                .roles("ADMIN")
                .build();

        final UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.inMemoryAuthentication()
               .withUser("spring")
               //
               .password("guru")
               .roles("ADMIN")
               .and()
               .withUser("user")
               //.password("{noop}password")
               .password("password")
               .roles("USER")
               .and()
               .withUser("scott")
               //.password("{noop}tiger")
               //.password("tiger")
               .password("{SSHA}o2Ziug2k2blgZwHuLVSeTlrjscuvHzRuUf5Zww==")
               .roles("CUSTOMER");
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        //return NoOpPasswordEncoder.getInstance();
        return new LdapShaPasswordEncoder();
    }
}
