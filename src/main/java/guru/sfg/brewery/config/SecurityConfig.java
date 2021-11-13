package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
                .password("{bcrypt}$2a$10$xxvx44xpSgliqwlhV1.r0e2g71uvcrnHnXIAdLjggJYfQggFheABC")
                .roles("ADMIN");

        auth.inMemoryAuthentication()
                .withUser("user")
                .password("{sha256}d12b6eed0bbecaa69f17df67cf559c3e64acaa1207c60fe0af566e5e13842954d5621f1d6f734aa7")
                .roles("USER");

        auth.inMemoryAuthentication()
                .withUser("scott")
                .password("{ldap}{SSHA}TXB7WGj+Uz59341jRlga2ZbZh/0zAiSXMrWOYw==")
                .roles("CUSTOMER");
    }


}
