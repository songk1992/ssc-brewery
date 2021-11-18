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
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.*;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.services.user.AuthorityService;
import guru.sfg.brewery.services.user.UserService;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Created by jt on 2019-01-26.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String EXAMPLE1_DISTRIBUTING = "example1 Distributing";
    public static final String EXAMPLE2_DISTRIBUTING = "example2 Distributing";
    public static final String EXAMPLE3_DISTRIBUTING = "example3 Distributing";
    public static final String EXAMPLE1_USER = "example1_username";
    public static final String EXAMPLE2_USER = "example2_username";
    public static final String EXAMPLE3_USER = "example3_username";


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

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        loadSecurityData();
        loadBreweryData();
        loadTastingRoomData();
        loadCustomerData();

    }

    private void loadCustomerData() {
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        // create customers
        Customer example1Customer = customerRepository.save(Customer.builder()
                .customerName(EXAMPLE1_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer example2Customer = customerRepository.save(Customer.builder()
                .customerName(EXAMPLE2_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer example3Customer = customerRepository.save(Customer.builder()
                .customerName(EXAMPLE3_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        // create Users
        User example1User = userRepository.save(User.builder().username(EXAMPLE1_USER)
                .password(passwordEncoder.encode("password"))
                .customer(example1Customer)
                .role(customerRole).build());

        User example2User = userRepository.save(User.builder().username(EXAMPLE2_USER)
                .password(passwordEncoder.encode("password"))
                .customer(example2Customer)
                .role(customerRole).build());

        User example3User = userRepository.save(User.builder().username(EXAMPLE3_USER)
                .password(passwordEncoder.encode("password"))
                .customer(example3Customer)
                .role(customerRole).build());

        // create orders
        createOrder(example1Customer);
        createOrder(example2Customer);
        createOrder(example3Customer);

        log.debug("Orders Loaded : " + beerOrderRepository.count());
    }

    private BeerOrder createOrder(Customer customer) {
        return beerOrderRepository.save(BeerOrder.builder()
                .customer(customer)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(Set.of(BeerOrderLine.builder()
                        .beer(beerRepository.findByUpc(BEER_1_UPC))
                        .orderQuantity(2)
                        .build()))
                .build());
    }

    private void loadSecurityData() {

        //beer auths
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().permission("beer.delete").build());

        // customer auths
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        // brewery auths
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());

        // beer order
        Authority createOrder = authorityRepository.save(Authority.builder().permission("order.create").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().permission("order.update").build());
        Authority readOrder = authorityRepository.save(Authority.builder().permission("order.read").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().permission("order.delete").build());
        Authority pickupOrder = authorityRepository.save(Authority.builder().permission("order.pickup").build());

        Authority createOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.create").build());
        Authority updateOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.update").build());
        Authority readOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.read").build());
        Authority deleteOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.delete").build());
        Authority pickupOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.pickup").build());

        // setup roles
        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(
                createBeer, readBeer, updateBeer, deleteBeer,
                createCustomer, readCustomer, updateCustomer, deleteCustomer,
                createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder, pickupOrder
        )));
        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery,
                createOrderCustomer, readOrderCustomer, updateOrderCustomer, deleteOrderCustomer, pickupOrderCustomer)));
        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        // build users and assign roles
        userRepository.save(User.builder().username("spring").password(passwordEncoder.encode("kimc")).role(adminRole).build());
        userRepository.save(User.builder().username("user").password(passwordEncoder.encode("password")).role(userRole).build());
        userRepository.save(User.builder().username("scott").password(passwordEncoder.encode("tiger")).role(customerRole).build());

        System.out.println(("Users Loaded : " + userRepository.count()));

        userRole.getAuthorities().forEach(authority -> System.out.println(authority.getPermission()));
    }


    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> beerOrderRepository.save(BeerOrder.builder()
                .customer(tastingRoom)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(Set.of(BeerOrderLine.builder()
                        .beer(beer)
                        .orderQuantity(2)
                        .build()))
                .build()));
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
