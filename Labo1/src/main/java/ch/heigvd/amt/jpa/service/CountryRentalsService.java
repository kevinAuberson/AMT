package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.util.List;

/**
 * Exercise Country by rentals.
 * Signature of methods (countryRentals_*) must not be changed.
 */
@ApplicationScoped
public class CountryRentalsService {

  @Inject
  private EntityManager em;

  public record CountryRentals(String country, Long rentals) {
  }

  public List<CountryRentals> countryRentals_NativeSQL() {
    String sql = "SELECT c.country, COUNT(r.rental_id) AS rentals " +
            "FROM country c " +
            "JOIN city ci ON c.country_id = ci.country_id " +
            "JOIN address a ON ci.city_id = a.city_id " +
            "JOIN customer cu ON a.address_id = cu.address_id " +
            "JOIN rental r ON cu.customer_id = r.customer_id " +
            "GROUP BY c.country, c.country_id " +
            "ORDER BY rentals DESC, c.country, c.country_id";

    List<Object[]> results = em.createNativeQuery(sql).getResultList();
    return results.stream()
            .map(result -> new CountryRentals((String) result[0], ((Number) result[1]).longValue()))
            .toList();
  }

  public List<CountryRentals> countryRentals_JPQL() {
    String jpql = "SELECT c.country, COUNT(r) AS rentals " +
            "FROM rental r " +
            "JOIN r.customer cu " +
            "JOIN cu.address a " +
            "JOIN a.city ci " +
            "JOIN ci.country c " +
            "GROUP BY c.country, c.id " +
            "ORDER BY COUNT(r.id) DESC, c.country, c.id";

    return em.createQuery(jpql, CountryRentals.class).getResultList();
  }

  public List<CountryRentals> countryRentals_CriteriaString() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
    Root<Rental> rental = cq.from(Rental.class);
    Join<Rental, Customer> customer = rental.join("customer");
    Join<Customer, Address> address = customer.join("address");
    Join<Address, City> city = address.join("city");
    Join<City, Country> country = city.join("country");

    cq.multiselect(country.get("country"), cb.count(rental.get("id")))
            .groupBy(country.get("country"), country.get("id"))
            .orderBy(cb.desc(cb.count(rental.get("id"))), cb.asc(country.get("country")), cb.asc(country.get("id")));

    List<Object[]> results = em.createQuery(cq).getResultList();
    return results.stream()
            .map(result -> new CountryRentals((String) result[0], ((Number) result[1]).longValue()))
            .toList();
  }

  public List<CountryRentals> countryRentals_CriteriaMetaModel() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
    Root<Rental> rental = cq.from(Rental.class);
    Join<Rental, Customer> customer = rental.join(Rental_.customer);
    Join<Customer, Address> address = customer.join(Customer_.address);
    Join<Address, City> city = address.join(Address_.city);
    Join<City, Country> country = city.join(City_.country);

    cq.multiselect(country.get(Country_.country), cb.count(rental.get(Rental_.id)))
            .groupBy(country.get(Country_.country), country.get(Country_.id))
            .orderBy(cb.desc(cb.count(rental.get(Rental_.id))), cb.asc(country.get(Country_.country)), cb.asc(country.get(Country_.id)));

    List<Object[]> results = em.createQuery(cq).getResultList();
    return results.stream()
            .map(result -> new CountryRentals((String) result[0], ((Number) result[1]).longValue()))
            .toList();
  }
}
