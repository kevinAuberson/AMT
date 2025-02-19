package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.Inventory;
import ch.heigvd.amt.jpa.entity.Store;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InventoryRepository {
    @Inject
    private EntityManager em;

    public record InventoryDTO (Integer id, Integer filmdId, Integer storeId) {
        public static InventoryDTO create(Integer filmdId, Integer storeId) {
            return new InventoryDTO(null, filmdId, storeId);
        }
    }

    private InventoryDTO fromEntityToDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return new InventoryDTO(inventory.getId(), inventory.getFilmId().getId(), inventory.getStoreId().getId());
    }

    public InventoryDTO read(Integer id) {
        Inventory inventory = em.find(Inventory.class, id);
        return this.fromEntityToDTO(inventory);
    }

    @Transactional
    public InventoryDTO create(InventoryDTO inventoryDTO) {
        Inventory inventory = new Inventory();
        inventory.setFilmId(em.find(Film.class, inventoryDTO.filmdId()));
        inventory.setStoreId(em.find(Store.class, inventoryDTO.storeId()));

        em.persist(inventory);
        return this.fromEntityToDTO(inventory);
    }

    @Transactional
    public void update(InventoryDTO inventoryDTO) {
        Inventory inventory = em.find(Inventory.class, inventoryDTO.id());

        if (inventory == null) {
            throw new IllegalArgumentException("Inventory with id " + inventoryDTO.id() + " does not exist");
        }

        inventory.setFilmId(em.find(Film.class, inventoryDTO.filmdId()));
        inventory.setStoreId(em.find(Store.class, inventoryDTO.storeId()));

        em.merge(inventory);
    }

    @Transactional
    public void delete(Integer id) {
        Inventory inventory = em.find(Inventory.class, id);

        if (inventory == null) {
            throw new IllegalArgumentException("Inventory with id " + id + " does not exist");
        }
        em.remove(inventory);
    }
}
