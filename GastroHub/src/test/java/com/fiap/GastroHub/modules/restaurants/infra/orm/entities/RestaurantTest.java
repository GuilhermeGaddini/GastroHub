package com.fiap.GastroHub.modules.restaurants.infra.orm.entities;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestaurantTest {

//    @Test
//    void testRestaurantEntity() {
//        assertThat(Restaurant.class, BeanMatchers.hasValidBeanConstructor());
//        assertThat(Restaurant.class, BeanMatchers.hasValidGettersAndSetters());
//    }

    @Test
    void testEntityGettersAndSetters() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("123 Street");
        restaurant.setCuisineType("Italian");
        restaurant.setOpeningHours("09:00 - 22:00");

        assertEquals(1L, restaurant.getId());
        assertEquals("Test Restaurant", restaurant.getName());
        assertEquals("123 Street", restaurant.getAddress());
        assertEquals("Italian", restaurant.getCuisineType());
        assertEquals("09:00 - 22:00", restaurant.getOpeningHours());
    }

    @Test
    void testEntityBuilder() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .address("123 Street")
                .cuisineType("Italian")
                .openingHours("09:00 - 22:00")
                .build();

        assertNotNull(restaurant);
        assertEquals("Test Restaurant", restaurant.getName());
    }
}

