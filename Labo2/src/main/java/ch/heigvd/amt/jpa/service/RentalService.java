package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.persistence.TypedQuery;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Signature of existing methods must not be changed.
 */
@ApplicationScoped
public class RentalService {

  @Inject
  EntityManager em;

  // The following records must not be changed
  public record RentalDTO (Integer inventory, Integer customer) {}
  public record FilmInventoryDTO(String title, String description, Integer inventoryId) {}
  public record CustomerDTO(Integer id, String firstName, String lastName) {}

  /**
   * Rent a film out of store's inventory for a given customer.
   *
   * @param inventory the inventory to rent
   * @param customer  the customer to which the inventory is rented
   * @param staff     the staff that process the customer's request in the store
   * @return an Optional that is present if rental is successful, if Optional is empty rental failed
   */

  @Transactional
  public Optional<RentalDTO> rentFilm(Inventory inventory, Customer customer, Staff staff) {
    try {
      Inventory lockedInventory = em.find(Inventory.class, inventory.getId(), LockModeType.PESSIMISTIC_WRITE);

      TypedQuery<Long> activeRentalsQuery = em.createQuery("""
        select count(r)
        from rental r
        where r.inventory.id = :inventoryId
          and r.returnDate is null
        """, Long.class);
      activeRentalsQuery.setParameter("inventoryId", inventory.getId());
      Long activeRentals = activeRentalsQuery.getSingleResult();

      if (activeRentals > 0) {
          return Optional.empty();
      }

      // Insérer une nouvelle location
      Rental rental = new Rental();
      rental.setInventory(lockedInventory);
      rental.setCustomer(customer);
      rental.setStaff(staff);
      rental.setRentalDate(new Timestamp(System.currentTimeMillis()));
      em.persist(rental);

      return Optional.of(new RentalDTO(lockedInventory.getId(), customer.getId()));
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }


  /**
   *
   * @param query the searched string
   * @return films matching the query
   */
  public List<FilmInventoryDTO> searchFilmInventory(String query, Store store) {
    String[] queryParts = query.split(" ");
    String sql = "SELECT f.title, f.description, i.inventory_id " +
            "FROM inventory i " +
            "JOIN film f ON i.film_id = f.film_id " +
            "JOIN store s ON i.store_id = s.store_id " +
            "JOIN address a ON s.address_id = a.address_id " +
            "JOIN city c ON a.city_id = c.city_id " +
            "WHERE i.store_id = ?1 AND " +
            "(LOWER(c.city) LIKE ?2 OR LOWER(f.title) LIKE ?2 OR LOWER(f.description) LIKE ?2 OR CAST(i.inventory_id AS VARCHAR) LIKE ?2)";

    for (int i = 0; i < queryParts.length; i++) {
      sql += " AND (LOWER(c.city) LIKE ?" + (i + 3) + " OR LOWER(f.title) LIKE ?" + (i + 3) + " OR LOWER(f.description) LIKE ?" + (i + 3) + " OR CAST(i.inventory_id AS VARCHAR) LIKE ?" + (i + 3) + ")";
    }

    var queryBuilder = em.createNativeQuery(sql);
    queryBuilder.setParameter(1, store.getId());
    queryBuilder.setParameter(2, "%" + queryParts[0].toLowerCase() + "%");

    for (int i = 0; i < queryParts.length; i++) {
      queryBuilder.setParameter(i + 3, "%" + queryParts[i].toLowerCase() + "%");
    }

    List<Object[]> results = queryBuilder.getResultList();

    return results.stream()
            .map(result -> new FilmInventoryDTO((String) result[0], (String) result[1], (Integer) result[2]))
            .toList();
  }

  public FilmInventoryDTO searchFilmInventory(Integer inventoryId) {
    String sql = "SELECT f.title, f.description, i.inventory_id " +
            "FROM inventory i " +
            "JOIN film f ON i.film_id = f.film_id " +
            "WHERE i.inventory_id = ?1";
    Object[] result = (Object[]) em.createNativeQuery(sql)
            .setParameter(1, inventoryId)
            .getSingleResult();
    return new FilmInventoryDTO((String) result[0], (String) result[1], (Integer) result[2]);
  }

  public CustomerDTO searchCustomer(Integer customerId) {
    String sql = "SELECT c.customer_id, c.first_name, c.last_name " +
            "FROM customer c WHERE c.customer_id = ?1";
    Object[] result = (Object[]) em.createNativeQuery(sql)
            .setParameter(1, customerId)
            .getSingleResult();
    return new CustomerDTO((Integer) result[0], (String) result[1], (String) result[2]);
  }

  public List<CustomerDTO> searchCustomer(String query, Store store) {
    String[] queryParts = query.split(" ");
    String sql = "SELECT c.customer_id, c.first_name, c.last_name " +
            "FROM customer c WHERE c.store_id = ?1 ";

    for (int i = 0; i < queryParts.length; i++) {
      sql += "AND (CAST(c.customer_id AS VARCHAR) LIKE ?" + (i + 2) +
              " OR UPPER(c.first_name) LIKE ?" + (i + 2) +
              " OR UPPER(c.last_name) LIKE ?" + (i + 2) + ")";
    }

    var queryObj = em.createNativeQuery(sql);
    queryObj.setParameter(1, store.getId());
    for (int i = 0; i < queryParts.length; i++) {
      queryObj.setParameter(i + 2,   queryParts[i].toUpperCase());
    }

    List<Object[]> results = queryObj.getResultList();
    return results.stream()
            .map(result -> new CustomerDTO((Integer) result[0], (String) result[1], (String) result[2]))
            .toList();
  }

  public Customer findCustomerById(Integer customerId) {
    return em.find(Customer.class, customerId);
  }

  public Inventory findInventoryById(Integer inventoryId) {
    return em.find(Inventory.class, inventoryId);
  }

  public Staff findStaffByUsername(String username) {
    TypedQuery<Staff> query = em.createQuery("SELECT s FROM Staff s WHERE s.username = :username", Staff.class);
    query.setParameter("username", username);
    return query.getSingleResult();
  }

  public Store findStoreByStaffUsername(String username) {
    TypedQuery<Store> query = em.createQuery("""
        SELECT s.store
        FROM Staff s
        WHERE s.username = :username
        """, Store.class);
    query.setParameter("username", username);
    return query.getSingleResult();
  }
}
