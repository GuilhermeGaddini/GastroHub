package com.fiap.GastroHub.helper;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;

public abstract class RoleTestHelper {

    public static Role generateRole() {
        return Role.builder().name("test_role").build();
    }
}
