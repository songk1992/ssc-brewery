package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                        .antMatchers("/beers/find", "/beers*").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
                )
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    // 방법 1
    //    @Override
    //    @Bean
    //    protected UserDetailsService userDetailsService() {
    //        UserDetails admin = User.withDefaultPasswordEncoder().username("spring").password("kimc").roles("ADMIN").build();
    //        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
    //        return new InMemoryUserDetailsManager(admin, user);
    //    }

    // 방법 2
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("kimc")
                .roles("ADMIN");

        auth.inMemoryAuthentication()
                .withUser("user")
                .password("$2a$10$7/LJUa.VBbT8uXkYkbb9zuu8CVEkcB58aR72T4reHHR8FSuZik3ua")
                .roles("USER");

        auth.inMemoryAuthentication()
                .withUser("scott")
                .password("tiger")
                .roles("CUSTOMER");
    }


}
