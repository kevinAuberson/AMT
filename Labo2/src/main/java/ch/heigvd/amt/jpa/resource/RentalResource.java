package ch.heigvd.amt.jpa.resource;

import ch.heigvd.amt.jpa.entity.Customer;
import ch.heigvd.amt.jpa.entity.Inventory;
import ch.heigvd.amt.jpa.entity.Staff;
import ch.heigvd.amt.jpa.entity.Store;
import ch.heigvd.amt.jpa.service.RentalService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.RestForm;


import java.util.List;
import java.util.Optional;

// The existing annotations on this class must not be changed (i.e. new ones are allowed)
@Path("rental")
public class RentalResource {

  @Inject
  RentalService rentalService;

  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance rental(String username);
    public static native TemplateInstance rental$success(RentalService.RentalDTO rental);
    public static native TemplateInstance rental$failure(String message);
    public static native TemplateInstance searchFilmsResults(
            List<RentalService.FilmInventoryDTO> films);
    public static native TemplateInstance searchFilmsSelect(
            RentalService.FilmInventoryDTO film);
    public static native TemplateInstance searchCustomersResults(
            List<RentalService.CustomerDTO> customers);
    public static native TemplateInstance searchCustomersSelect(
            RentalService.CustomerDTO customer);
  }

  @Inject
  @Location("RentalResource/simple.txt")
  Template simple;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public TemplateInstance rental(@Context SecurityContext securityContext) {
    return Templates.rental(securityContext.getUserPrincipal().getName());
  }

  @POST
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  @Authenticated
  public TemplateInstance registerRental(@Context SecurityContext securityContext,
                                         @RestForm Integer inventory, @RestForm Integer customer) {
    if (inventory == null || customer == null) {
      return Templates.rental$failure("The submission is not valid, missing inventory or customer");
    }

    // Retrieve the staff member from the security context
    Staff staff = rentalService.findStaffByUsername(securityContext.getUserPrincipal().getName());

    // Find the inventory and customer entities
    Inventory inventoryEntity = rentalService.findInventoryById(inventory);
    Customer customerEntity = rentalService.findCustomerById(customer);

    // Attempt to rent the film
    Optional<RentalService.RentalDTO> rentalResult = rentalService.rentFilm(inventoryEntity, customerEntity, staff);

    if (rentalResult.isPresent()) {
      return simple.data("content", "The rental of inventory "+ inventory + " by customer " + customer + " was successfully registered");
    } else {
      return Templates.rental$failure("The selected item is not available.");
    }
  }

  @GET
  @Path("/film/{inventory}")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance selectFilmsGet(@PathParam("inventory") Integer inventory) {
    RentalService.FilmInventoryDTO film = rentalService.searchFilmInventory(inventory);
    return Templates.searchFilmsSelect(film);
  }

  @POST
  @Path("/film/search")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance searchFilmsPost(@Context SecurityContext securityContext, @RestForm String query) {
    String username = securityContext.getUserPrincipal().getName();
    Store store = rentalService.findStoreByStaffUsername(username);
    List<RentalService.FilmInventoryDTO> films = rentalService.searchFilmInventory(query, store);
    return Templates.searchFilmsResults(films);
  }

  @POST
  @Path("/customer/search")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance searchCustomersPost(@Context SecurityContext securityContext, @RestForm String query) {
    String username = securityContext.getUserPrincipal().getName();
    Store store = rentalService.findStoreByStaffUsername(username);
    List<RentalService.CustomerDTO> customers = rentalService.searchCustomer(query, store);
    return Templates.searchCustomersResults(customers);
  }

  @GET
  @Path("/customer/{customer}")
  @Produces(MediaType.TEXT_HTML)
  @Blocking
  public TemplateInstance selectCustomerGet(@PathParam("customer") Integer customer) {
    RentalService.CustomerDTO customerDTO = rentalService.searchCustomer(customer);
    return Templates.searchCustomersSelect(customerDTO);
  }
}
