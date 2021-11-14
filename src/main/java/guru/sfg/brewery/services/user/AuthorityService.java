package guru.sfg.brewery.services.user;

import guru.sfg.brewery.domain.security.Authority;

import java.util.List;
import java.util.Set;

public interface AuthorityService {
    List<Authority> saveAuthorityList(List<Authority> AuthorityList);

    Set<Authority> getAuthoritySetByRole(String role);
}
