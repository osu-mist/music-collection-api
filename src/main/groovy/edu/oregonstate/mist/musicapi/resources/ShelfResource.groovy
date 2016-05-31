package edu.oregonstate.mist.musicapi.resources

import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.musicapi.core.Shelf
import edu.oregonstate.mist.api.AuthenticatedUser
import io.dropwizard.auth.Auth
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ResponseBuilder
import org.skife.jdbi.v2.DBI

/**
 * Sample resource class.
 */
@Path('/shelf/{id}')
class ShelfResource extends Resource {
    private DBI dbi

    public ShelfResource(DBI dbi) {
        this.dbi = dbi
    }

    /**
     * Responds to GET requests by returning a message.
     *
     * @return message
     */
    @GET
    @Produces("application/json")
    Response getShelf(@Auth AuthenticatedUser authenticatedUser, @PathParam("id") int id) {
        Shelf result

        def h = this.dbi.open()
        try {
            def q = h.createQuery('SELECT id, name FROM MUS_SHELF WHERE id = ?')
            q.bind(0, id)
            result = q.map(Shelf).first()
        } finally {
            h.close()
        }

        if (!result) {
            return this.notFound(result)()
        }
        return this.ok(result).build()
    }
}
