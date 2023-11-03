package com.smartbyte.edubookschedulerbackend.domain;

import lombok.Getter;
import lombok.Setter;


public enum Role {
    Student(0),
    Tutor(1),
    Admin(2);


    private final int roleId;

    Role(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }

    public static Role fromRoleId(int roleId) {
        for (Role role : Role.values()) {
            if (role.getRoleId() == roleId) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role with id " + roleId);
    }
}
