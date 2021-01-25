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
    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTORS = "Key West Distributors";
    public static final String STPETE_USER = "stpete";
    public static final String DUNEDIN_USER = "dunedin";
    public static final String KEYWEST_USER = "keywest";

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        loadAuthoritiesAndUsers();
        loadBreweryData();
        loadCustomerData();
    }


    private void loadCustomerData() {
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        //create customers
        Customer stPeteCustomer = customerRepository.save(Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer dunedinCustomer = customerRepository.save(Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer keyWestCustomer = customerRepository.save(Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTORS)
                .apiKey(UUID.randomUUID())
                .build());

        //create users
        User stPeteUser = userRepository.save(User.builder().username("stpete")
                .password(passwordEncoder.encode("password"))
                .customer(stPeteCustomer)
                .role(customerRole).build());

        User dunedinUser = userRepository.save(User.builder().username("dunedin")
                .password(passwordEncoder.encode("password"))
                .customer(dunedinCustomer)
                .role(customerRole).build());

        User keywest = userRepository.save(User.builder().username("keywest")
                .password(passwordEncoder.encode("password"))
                .customer(keyWestCustomer)
                .role(customerRole).build());

        //create orders
        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);

        log.debug("Orders Loaded: " + beerOrderRepository.count());
    }


    private BeerOrder createOrder(Customer customer) {
        return  beerOrderRepository.save(BeerOrder.builder()
                .customer(customer)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(Set.of(BeerOrderLine.builder()
                        .beer(beerRepository.findByUpc(BEER_1_UPC))
                        .orderQuantity(2)
                        .build()))
                .build());
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

    private void loadAuthoritiesAndUsers() {
        if (authorityRepository.findAll().isEmpty()) {
            // Beer Auths
            final Authority createBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.create").build());
            final Authority readBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.read").build());
            final Authority updateBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.update").build());
            final Authority deleteBeerAuthority = authorityRepository.save(Authority.builder().permission("beer.delete").build());

            // Customer Auths
            final Authority createCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.create").build());
            final Authority readCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.read").build());
            final Authority updateCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.update").build());
            final Authority deleteCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.delete").build());

            // Brewery Auths
            final Authority createBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.create").build());
            final Authority readBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.read").build());
            final Authority updateBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.update").build());
            final Authority deleteBreweryAuthority = authorityRepository.save(Authority.builder().permission("brewery.delete").build());

            // Order Auths
            final Authority createOrderAuthority = authorityRepository.save(Authority.builder().permission("order.create").build());
            final Authority readOrderAuthority = authorityRepository.save(Authority.builder().permission("order.read").build());
            final Authority updateOrderAuthority = authorityRepository.save(Authority.builder().permission("order.update").build());
            final Authority deleteOrderAuthority = authorityRepository.save(Authority.builder().permission("order.delete").build());
            final Authority pickupOrderAuthority = authorityRepository.save(Authority.builder().permission("order.pickup").build());

            final Authority createOrderCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.order.create").build());
            final Authority readOrderCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.order.read").build());
            final Authority updateOrderCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.order.update").build());
            final Authority deleteOrderCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.order.delete").build());
            final Authority pickupOrderCustomerAuthority = authorityRepository.save(Authority.builder().permission("customer.order.pickup").build());

            // Roles
            final Role roleAdmin = roleRepository.save(Role.builder().name("ADMIN").build());
            final Role roleCustomer = roleRepository.save(Role.builder().name("CUSTOMER").build());
            final Role roleUser = roleRepository.save(Role.builder().name("USER").build());

            roleAdmin.setAuthorities(
                    new HashSet<>(
                            Set.of(
                                    createBeerAuthority, updateBeerAuthority, readBeerAuthority, deleteBeerAuthority,
                                    createCustomerAuthority, readCustomerAuthority, updateCustomerAuthority, deleteCustomerAuthority,
                                    createBreweryAuthority, readBreweryAuthority, updateBreweryAuthority, deleteBreweryAuthority ,
                                    createOrderAuthority, readOrderAuthority, updateOrderAuthority, deleteOrderAuthority, pickupOrderAuthority
                            )
                    )
            );
            roleCustomer.setAuthorities(
                    new HashSet<>(
                            Set.of(
                                    readBeerAuthority, readCustomerAuthority, readBreweryAuthority,
                                    createOrderCustomerAuthority, readOrderCustomerAuthority, updateOrderCustomerAuthority, deleteOrderCustomerAuthority, pickupOrderCustomerAuthority
                            )
                    )
            );
            roleUser.setAuthorities(
                    new HashSet<>(
                            Set.of(readBeerAuthority)
                    )
            );

            roleRepository.saveAll(Arrays.asList(roleAdmin, roleCustomer, roleUser));


            final User springUser = User.builder()
                    .username("spring")
                    .password(passwordEncoder.encode("guru"))
                    .role(roleAdmin)
                    .build();

            final User regularUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("password"))
                    .role(roleUser)
                    .build();

            final User scottUser = User.builder()
                    .username("scott")
                    .password(passwordEncoder.encode("tiger"))
                    .role(roleCustomer)
                    .build();

            userRepository.save(springUser);
            userRepository.save(regularUser);
            userRepository.save(scottUser);
        }
    }


}
