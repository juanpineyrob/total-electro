package com.totalelectro.dto;

import lombok.Getter;
import lombok.Setter;
import com.totalelectro.model.User;
import com.totalelectro.model.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String address;
    private String phoneNumber;
    private Set<RoleDTO> roles;

    public static UserDetailDTO fromUser(User user) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCity(user.getCity());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                .map(RoleDTO::fromRole)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }

    @Getter
    @Setter
    public static class RoleDTO {
        private Long id;
        private String name;

        public static RoleDTO fromRole(Role role) {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setName(role.getName());
            return dto;
        }
    }
} 