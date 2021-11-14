package guru.sfg.brewery.services.user;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl implements AuthorityService{
    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }
    @Override
    public List<Authority> saveAuthorityList(List<Authority> authorityList) {
        return (authorityRepository.saveAll(authorityList));
    }
    @Override
    public Set<Authority> getAuthoritySetByRole(String role) {
        return authorityRepository.findAll().stream().filter(authority -> authority.getRole().equals(role)).collect(Collectors.toSet());
    }
}
