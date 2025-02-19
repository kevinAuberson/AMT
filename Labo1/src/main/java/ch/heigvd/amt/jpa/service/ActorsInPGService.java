package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.Actor;
import ch.heigvd.amt.jpa.entity.Actor_;
import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.Film_;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.Collections;
import java.util.List;

/**
 * Exercise Actors with films of PG rating.
 * Signature of methods (actorInPGRatings_*) must not be changed.
 */
@ApplicationScoped
public class ActorsInPGService {

  @Inject
  private EntityManager em;

  public record ActorInPGRating(String firstName, String lastName, Long nbFilms) {
  }

  public List<ActorInPGRating> actorInPGRatings_NativeSQL() {
    String sql = """
        SELECT a.first_name, a.last_name, COUNT(f.film_id) AS nbFilms
        FROM actor a
        JOIN film_actor fa ON a.actor_id = fa.actor_id
        JOIN film f ON fa.film_id = f.film_id
        WHERE f.rating = 'PG'
        GROUP BY a.first_name, a.last_name, a.actor_id
        ORDER BY nbFilms DESC, a.first_name, a.last_name, a.actor_id
    """;

    List<Object[]> results = em.createNativeQuery(sql).getResultList();
    return results.stream()
            .map(result -> new ActorInPGRating((String) result[0], (String) result[1], ((Number) result[2]).longValue()))
            .toList();
  }

  public List<ActorInPGRating> actorInPGRatings_JPQL() {
    TypedQuery<Object[]> query = em.createQuery("""
        SELECT a.firstName, a.lastName, COUNT(f.id) AS nbFilms
        FROM actor a
        JOIN a.films f
        WHERE f.rating = 'PG'
        GROUP BY a.firstName, a.lastName, a.id
        ORDER BY nbFilms DESC, a.firstName, a.lastName, a.id
    """, Object[].class);

    List<Object[]> results = query.getResultList();
    return results.stream()
            .map(result -> new ActorInPGRating((String) result[0], (String) result[1], ((Number) result[2]).longValue()))
            .toList();
  }

  public List<ActorInPGRating> actorInPGRatings_CriteriaString() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
    Root<Actor> actor = cq.from(Actor.class);
    Join<Actor, Film> film = actor.join("films");

    cq.multiselect(actor.get("firstName"), actor.get("lastName"), cb.count(film.get("id")))
            .where(cb.equal(film.get("rating"), "PG"))
            .groupBy(actor.get("firstName"), actor.get("lastName"), actor.get("id"))
            .orderBy(cb.desc(cb.count(film.get("id"))), cb.asc(actor.get("firstName")), cb.asc(actor.get("lastName")), cb.asc(actor.get("id")));

    TypedQuery<Object[]> query = em.createQuery(cq);
    List<Object[]> results = query.getResultList();
    return results.stream()
            .map(result -> new ActorInPGRating((String) result[0], (String) result[1], ((Number) result[2]).longValue()))
            .toList();
  }

  public List<ActorInPGRating> actorInPGRatings_CriteriaMetaModel() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
    Root<Actor> actor = cq.from(Actor.class);
    Join<Actor, Film> film = actor.join(Actor_.films);

    cq.multiselect(actor.get(Actor_.firstName), actor.get(Actor_.lastName), cb.count(film.get(Film_.id)))
            .where(cb.equal(film.get(Film_.rating), "PG"))
            .groupBy(actor.get(Actor_.firstName), actor.get(Actor_.lastName), actor.get(Actor_.id))
            .orderBy(cb.desc(cb.count(film.get(Film_.id))), cb.asc(actor.get(Actor_.firstName)), cb.asc(actor.get(Actor_.lastName)), cb.asc(actor.get(Actor_.id)));

    TypedQuery<Object[]> query = em.createQuery(cq);
    List<Object[]> results = query.getResultList();
    return results.stream()
            .map(result -> new ActorInPGRating((String) result[0], (String) result[1], ((Number) result[2]).longValue()))
            .toList();
  }
}
