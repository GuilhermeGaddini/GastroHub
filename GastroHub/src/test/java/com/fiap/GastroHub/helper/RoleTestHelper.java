package com.fiap.GastroHub.helper;

import com.fiap.GastroHub.modules.roles.dtos.CreateUpdateRoleRequest;
import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;

public abstract class RoleTestHelper {

    public static Role generateRole() {
        return Role.builder().name("test_role").build();
    }

    public static Role generateFullRole() {
        return Role.builder().id(1L).name("test_role").build();
    }

    public static CreateUpdateRoleRequest generateCreateUpdateRoleRequest() {
        CreateUpdateRoleRequest request = new CreateUpdateRoleRequest();
        request.setName("test_role");
        return request;
    }
}
