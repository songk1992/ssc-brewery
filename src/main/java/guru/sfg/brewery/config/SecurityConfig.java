package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;

    // needed for use with Spring Data JPA SPeL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }


    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class).csrf().disable();


        http.cors().and()
                .authorizeRequests(authorize -> authorize
                                .antMatchers("/h2-console/**").permitAll() // do not use in production
                                .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()

//                        .mvcMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
//                        .antMatchers(HttpMethod.GET, "/brewery/breweries").hasAnyRole("ADMIN", "CUSTOMER")
//                        .antMatchers(HttpMethod.GET, "/brewery/api/v1/breweries").hasAnyRole("ADMIN", "CUSTOMER")
//                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
//                        .mvcMatchers("/beers/find", "/beers/{beerId}").hasAnyRole("ADMIN","CUSTOMER","USER")
                )
                // Annotation 기반이 이것보다 깔끔함


                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(loginConfigurer -> loginConfigurer
                        .loginProcessingUrl("/login")
                        .loginPage("/")
                        .permitAll()
                        .successForwardUrl("/")
                        .defaultSuccessUrl("/")
                        .failureUrl("/?error")
                )
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/?logout")
                        .permitAll())
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
                .and().rememberMe().tokenRepository(persistentTokenRepository).userDetailsService(userDetailsService);
        //.rememberMe()
        //.key("sfg-key")
        //.userDetailsService(userDetailsService);

        // h2-console config
        http.headers().frameOptions().sameOrigin();
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
    //    @Override
    //        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //            auth.inMemoryAuthentication()
    //                    .withUser("spring")
    //                    .password("{bcrypt}$2a$10$xxvx44xpSgliqwlhV1.r0e2g71uvcrnHnXIAdLjggJYfQggFheABC")
    //                    .roles("ADMIN");
    //
    //            auth.inMemoryAuthentication()
    //                    .withUser("user")
    //                    .password("{sha256}d12b6eed0bbecaa69f17df67cf559c3e64acaa1207c60fe0af566e5e13842954d5621f1d6f734aa7")
    //                    .roles("USER");
    //
    //            auth.inMemoryAuthentication()
    //                    .withUser("scott")
    //                    .password("{bcrypt10}$2a$10$VycvjtCj1NJSfqcl9n4yv..v4n4cYxmEC9xJoA0mCKYOZVPikee/a")
    //                    .roles("CUSTOMER");
    //       }


}
