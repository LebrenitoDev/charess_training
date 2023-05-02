package org.charess.training.domain.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="users")
public class User extends Audit implements Serializable, UserDetails {

    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private Person person;

    @NotEmpty
    @Size(min = 3, max = 30)
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotEmpty
    @Column(name = "password", length = 250, nullable = false)
    private String password;

    @NotEmpty
    @Column(name = "locale", nullable = false, length = 2)
    private String locale = Locale.FRANCAIS.getLocale();

    @Column(name = "status", length = 15, nullable = false)
    private String status = Status.USER_LOCKED.toString();

    @ManyToOne
    @JoinColumn(name = "profile")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "institution")
    private Place institution;

    @Transient
    private String token;

    private Integer activatedBy;

    @Column(name = "retired",  nullable = false , columnDefinition = "boolean default false")
    private boolean retired = false;

    public Boolean setRetired(boolean retired) {
        return this.retired = retired;
    }

    public Boolean isRetired() {
        return this.retired;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public Integer getRetiredBy() {
        return this.retiredBy;
    }

    public void setRetiredBy(Integer retiredBy) {
        this.retiredBy = retiredBy;
    }

    private Integer retiredBy;

    @Column(name = "activated_date")
    private LocalDateTime activatedDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Collection<Profile> getAuthorities() {
        List<Profile> authorities = new ArrayList<Profile>();
        if(profile != null)
            authorities.add(profile);
        return authorities;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        boolean isLocked = false;
        if (Status.USER_ACTIVE.toString().equals(this.status) || Status.USER_PENDING.toString().equals(this.status))
            isLocked = true;
        return isLocked;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        boolean bool = true;
        if(getAuthorities().size() < 1)
            bool = false;
        return bool;
    }

    public Place getInstitution() {
        return institution;
    }

    public void setInstitution(Place institution) {
        this.institution = institution;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getActivatedBy() {
        return activatedBy;
    }

    public void setActivatedBy(Integer activatedBy) {
        this.activatedBy = activatedBy;
    }

    public LocalDateTime getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(LocalDateTime activatedDate) {
        this.activatedDate = activatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", person=" + person +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", locale='" + locale + '\'' +
                ", status='" + status + '\'' +
                ", profile=" + profile +
                ", token='" + token + '\'' +
                '}';
    }
}
