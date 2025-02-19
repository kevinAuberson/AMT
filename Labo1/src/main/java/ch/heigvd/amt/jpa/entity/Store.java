package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "manager_staff_id")
    private Staff managerStaffId;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address addressId;

    @Column(name = "last_update", nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime lastUpdate;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Staff getManagerStaffId() {
        return managerStaffId;
    }

    public void setManagerStaffId(Staff managerStaffId) {
        this.managerStaffId = managerStaffId;
    }

    public Address getAddressId() {
        return addressId;
    }

    public void setAddressId(Address addressId) {
        this.addressId = addressId;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", managerStaffId=" + managerStaffId +
                ", addressId=" + addressId +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}