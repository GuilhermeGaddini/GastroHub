package com.fiap.GastroHub.helper;

import com.fiap.GastroHub.modules.users.dtos.CreateUpdateUserRequest;
import com.fiap.GastroHub.modules.users.dtos.UserResponse;
import com.fiap.GastroHub.modules.users.infra.orm.entities.User;

import java.util.Date;

public abstract class UserTestHelper {
    public static CreateUpdateUserRequest generateCreateUpdateUserRequest() {
        CreateUpdateUserRequest userDto;
        userDto = new CreateUpdateUserRequest();
        userDto.setName("John Doe");
        userDto.setAddress("123 Main Street");
        userDto.setEmail("johndoe@example.com");
        userDto.setPassword("securepassword");
        userDto.setCreatedAt(null); // O campo será gerado automaticamente no método
        userDto.setUpdatedAt(null);
        return userDto;
    }

    public static User generateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setAddress("123 Main Street");
        user.setEmail("johndoe@example.com");
        user.setPassword("securepassword");
        return user;
    }

    public static User generateUser(CreateUpdateUserRequest userRequest) {
        User user = new User();
        user.setId(1L);
        user.setName(userRequest.getName());
        user.setAddress(userRequest.getAddress());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setCreatedAt(new Date());
        user.setLastUpdatedAt(user.getCreatedAt());

        return user;
    }

    public static UserResponse generateUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setAddress(user.getAddress());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }
}
