package ch.heigvd.amt.jpa.entity;

import io.quarkus.security.jpa.RolesValue;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role", nullable = false, unique = true)
    @RolesValue
    private String role;

    @ManyToMany(mappedBy = "roles")
    private List<Staff> staff;

    public Role(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }
}
