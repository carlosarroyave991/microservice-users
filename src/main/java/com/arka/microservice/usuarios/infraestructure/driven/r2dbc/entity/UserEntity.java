package com.arka.microservice.usuarios.infraestructure.driven.r2dbc.entity;

import com.arka.microservice.usuarios.domain.models.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

//Agregado raiz
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "users")
public class UserEntity {
    @Id
    private Long id;
    private String name;

    @Column("user_type")
    private UserType userType;

    private String email;
    private String phone;
    private String dni;
    private String username;
    private String password;

    // Si user es el agregado raiz, se incluira el shippingaddress en esta entidad y no en address
    // Estrategia que evita referencias ciclicas y simplifica la persistencia.
    @Transient
    @MappedCollection(idColumn = "user_id")
    private List<ShippingAddressEntity> shippingAddresses;

    public UserEntity() {
    }

    public UserEntity(Long id, List<ShippingAddressEntity> shippingAddresses, String password, String username, String dni, String phone, String email, UserType userType, String name) {
        this.id = id;
        this.shippingAddresses = shippingAddresses;
        this.password = password;
        this.username = username;
        this.dni = dni;
        this.phone = phone;
        this.email = email;
        this.userType = userType;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ShippingAddressEntity> getShippingAddresses() {
        return shippingAddresses;
    }

    public void setShippingAddresses(List<ShippingAddressEntity> shippingAddresses) {
        this.shippingAddresses = shippingAddresses;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}