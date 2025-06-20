package com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp;

import com.arka.microservice.usuarios.domain.models.AddressModel;
import com.arka.microservice.usuarios.domain.models.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class UserWithAddressResponseDto {
    private Long id;
    private String name;
    private UserType userType;
    private String email;
    private String phone;
    private String dni;
    private String username;
    private List<AddressResponseDto> shippingAddresses;

    public List<AddressResponseDto> getShippingAddresses() {
        return shippingAddresses;
    }

    public void setShippingAddresses(List<AddressResponseDto> shippingAddresses) {
        this.shippingAddresses = shippingAddresses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
