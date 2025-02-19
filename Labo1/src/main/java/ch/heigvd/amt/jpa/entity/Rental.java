package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Integer id;

    @Column(name = "rental_date", nullable = false)
    private LocalDateTime rentalDate;

    @OneToOne(optional=false, fetch=LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @OneToOne(optional=false, fetch=LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @OneToOne(optional=false, fetch=LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", rentalDate=" + rentalDate +
                ", inventory=" + inventory +
                ", customer=" + customer +
                ", returnDate=" + returnDate +
                ", staff=" + staff +
                '}';
    }
}