package guru.sfg.brewery.config;


import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import guru.sfg.brewery.security.URLParameterAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestHeaderAuthFilter restHeaderAuthFilter(final AuthenticationManager authenticationManager) {
        final RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);

        return filter;
    }

    public URLParameterAuthFilter urlParameterAuthFilter(final AuthenticationManager authenticationManager) {
        final URLParameterAuthFilter filter = new URLParameterAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);

        return filter;
    }

    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(urlParameterAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

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
               //.password("guru")
               .password("{bcrypt}$2a$10$cbcOXvctYzE2lPdgnoSfluKD2gnxhivwNQsj55S8D/xba.iZvf/22")
               .roles("ADMIN")
               .and()
               .withUser("user")
               //.password("{noop}password")
               //.password("password")
               .password("{sha256}74e173cf2f0b6b3bb8fad83f2f7ec87e5349e486278a7810fb88e4f28ccbfa6b5956ce50821b6771")
               .roles("USER")
               .and()
               .withUser("scott")
               //.password("{noop}tiger")
               //.password("tiger")
               //.password("{ldap}{SSHA}o2Ziug2k2blgZwHuLVSeTlrjscuvHzRuUf5Zww==")
               .password("{bcrypt10}$2a$10$q5egRLf4Nl3O0p7oQIWQkeEfeJ0Swn8/aPFTs.KwaJvaLIRFof4hm")
               .roles("CUSTOMER");
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        //return NoOpPasswordEncoder.getInstance();
        //return new LdapShaPasswordEncoder();
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();

        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
