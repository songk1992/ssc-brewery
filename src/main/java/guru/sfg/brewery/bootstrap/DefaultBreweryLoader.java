/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.*;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.*;
import guru.sfg.brewery.services.user.AuthorityService;
import guru.sfg.brewery.services.user.UserService;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Created by jt on 2019-01-26.
 */
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;

    private final UserService userService;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadBreweryData();
        loadCustomerData();

        authorityService.saveAuthorityList(getAuthorityDataList());
        userService.saveUserList(getUserDataList());

    }

    private List<User> getUserDataList() {

        System.out.println("init user data");
        List<User> userList = new ArrayList<>();

        User user = new User();
        user.setUsername("spring");
        user.setPassword(passwordEncoder.encode("kimc"));
        user.setAuthorities(authorityService.getAuthoritySetByRole("ADMIN"));
        userList.add(user);

        User user2 = new User();
        user2.setUsername("user");
        user2.setPassword("{sha256}d12b6eed0bbecaa69f17df67cf559c3e64acaa1207c60fe0af566e5e13842954d5621f1d6f734aa7");
        user2.setAuthorities(authorityService.getAuthoritySetByRole("USER"));
        userList.add(user2);

        User user3 = new User();
        user3.setUsername("scott");
        user3.setPassword("{bcrypt10}$2a$10$VycvjtCj1NJSfqcl9n4yv..v4n4cYxmEC9xJoA0mCKYOZVPikee/a");
        user3.setAuthorities(authorityService.getAuthoritySetByRole("CUSTOMER"));
        userList.add(user3);

        return userList;
    }

    private List<Authority> getAuthorityDataList() {
        System.out.println("init authority data");
        List<Authority> authorityList = new ArrayList<>();

        Authority authUser = new Authority();
        authUser.setRole("USER");
        authorityList.add(authUser);

        Authority authAdmin = new Authority();
        authAdmin.setRole("ADMIN");
        authorityList.add(authAdmin);

        Authority authCustomer = new Authority();
        authCustomer.setRole("CUSTOMER");
        authorityList.add(authCustomer);

        return authorityList;
    }

    private void loadCustomerData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }
}
