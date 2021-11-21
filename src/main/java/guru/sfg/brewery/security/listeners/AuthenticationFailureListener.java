package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("login failure");
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {

            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof String) {
                builder.username(token.getPrincipal().toString());

                Optional<User> user = userRepository.findByUsername(token.getPrincipal().toString());

                user.ifPresent(builder::user);

                log.debug("attempted username : " + token.getPrincipal());
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                builder.sourceIp(details.getRemoteAddress());

                log.debug("Source IP : " + details.getRemoteAddress());
            }

            LoginFailure loginFailure = loginFailureRepository.save(builder.build());
            log.debug("Login Failure : " + loginFailure.getUsername());

            if (loginFailure.getUser() != null) {
                lockUserAccount(loginFailure.getUser());
            }
        }
    }

    private void lockUserAccount(User user) {
        List<LoginFailure> failureList = loginFailureRepository.findAllByuserAndCreatedDateAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        if (failureList.size() > 3) {
            log.debug("Locking User Account ... ");
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }


}
