package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Film getFilmId() {
        return film;
    }

    public void setFilmId(Film film) {
        this.film = film;
    }

    public Store getStoreId() {
        return store;
    }

    public void setStoreId(Store store) {
        this.store = store;
    }

    // Create toString method following the same pattern as the other classes
    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", film=" + film +
                ", store=" + store +
                '}';
    }
}