package org.charess.training.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="profile")
public class Profile extends ID implements Serializable, GrantedAuthority {

    @Column(name = "role", length = 60, unique = true, nullable = false)
    private String role;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "page", length = 100, nullable = false)
    private String page;

    @Autowired
    public String getAuthority() {
        return "ROLE_"+role.toUpperCase();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

}
