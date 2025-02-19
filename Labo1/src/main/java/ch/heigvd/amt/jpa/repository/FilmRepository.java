package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.Language;
import ch.heigvd.amt.jpa.entity.MpaaRating;
import ch.heigvd.amt.jpa.entity.MpaaRatingConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FilmRepository {
    @Inject
    private EntityManager em;

    public record FilmDTO (Integer id, String title, String language, String rating) {
    }

    public FilmDTO read(Integer id) {
        Film film = em.find(Film.class, id);

        if(film == null){
            return null;
        }

        return new FilmDTO(film.getId(), film.getTitle(), film.getLanguage().getName(), film.getRating().toString());
    }

    @Transactional
    public Integer create(String title, String language, String rating) {
        Film film = new Film();
        film.setTitle(title);

        language = language.trim().toLowerCase();

        TypedQuery<Language> queryLanguage = em.createQuery("SELECT l FROM language l WHERE TRIM(LOWER(l.name)) = :name", Language.class);

        queryLanguage.setParameter("name", language);

        Language lang = null;

        try {
            lang = queryLanguage.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No language found with name: " + language + ". Creating a new one.");
        }

        film.setLanguage(lang);

        film.setRating(MpaaRating.getFromName(rating));

        em.persist(film);

        return film.getId();
    }

    @Transactional
    public void update(Integer id, String title, String language, String rating) {
        Film film = em.find(Film.class, id);

        if (film == null) {
            throw new IllegalArgumentException("Film with id " + id + " does not exist");
        }

        film.setTitle(title);

        language = language.trim().toLowerCase();

        TypedQuery<Language> queryLanguage = em.createQuery("SELECT l FROM language l WHERE TRIM(LOWER(l.name)) = :name", Language.class);

        queryLanguage.setParameter("name", language);

        Language lang = null;

        try {
            lang = queryLanguage.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No language found with name: " + language + ". Creating a new one.");
        }

        film.setLanguage(lang);

        film.setRating(MpaaRating.getFromName(rating));

        em.merge(film);
    }

    @Transactional
    public void delete(Integer id) {
        Film film = em.find(Film.class, id);

        if (film == null) {
            throw new IllegalArgumentException("Film with id " + id + " does not exist");
        }
        em.remove(film);
    }
}
