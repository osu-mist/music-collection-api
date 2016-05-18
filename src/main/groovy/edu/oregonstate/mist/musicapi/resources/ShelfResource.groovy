package edu.oregonstate.mist.musicapi.resources

import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.musicapi.core.Shelf
import edu.oregonstate.mist.api.AuthenticatedUser
import io.dropwizard.auth.Auth
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ResponseBuilder

/**
 * Sample resource class.
 */
@Path('/shelf/{id}')
class ShelfResource extends Resource {
    /**
     * Responds to GET requests by returning a message.
     *
     * @return message
     */
    @GET
    @Produces("application/json")
    public Response get(@Auth AuthenticatedUser authenticatedUser) {
        ResponseBuilder responseBuilder = ok(new Shelf())
        return responseBuilder.build()
    }
}
