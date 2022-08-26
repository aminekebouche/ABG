package io.github.oliviercailloux.abg.resources;

import io.github.oliviercailloux.abg.model.MoveEntity;
import io.github.oliviercailloux.abg.service.MoveService;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/moves")
@RequestScoped
public class MoveResource {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(MoveResource.class);

  @Context
  UriInfo uriInfo;

  @Inject
  MoveService moveS;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public List<Integer> getMoves() {
    LOGGER.info("Running GET.");
    final List<MoveEntity> allMoves = moveS.getAll();
    LOGGER.info("Returning {} items.", allMoves.size());
    return allMoves.stream().map(mv -> mv.getId()).collect(Collectors.toList());
  }

  @POST
  @Produces(MediaType.TEXT_PLAIN)
  public Response postMove() {
    LOGGER.info("Running POST.");
    final MoveEntity move = new MoveEntity();

    moveS.persist(move);

    LOGGER.info("Redirecting.");
    return Response.seeOther(uriInfo.getAbsolutePath()).build();
  }
}
