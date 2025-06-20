package com.arka.microservice.usuarios.domain.models;

import com.arka.microservice.usuarios.domain.models.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class UserWithAddressesModel {
    private Long id;
    private String name;
    private UserType userType;
    private String email;
    private String phone;
    private String dni;
    private String username;
    private List<AddressModel> shippingAddresses;

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

    public List<AddressModel> getShippingAddresses() {
        return shippingAddresses;
    }

    public void setShippingAddresses(List<AddressModel> shippingAddresses) {
        this.shippingAddresses = shippingAddresses;
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
