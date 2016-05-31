package edu.oregonstate.mist.musicapi.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.musicapi.core.Album
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

@Path('/album/{id}')
class AlbumResource extends Resource {
    private DBI dbi

    public AlbumResource(DBI dbi) {
        this.dbi = dbi
    }

    /**
     * Responds to GET requests by returning an album
     *
     * @return album
     */
    @GET
    @Produces("application/json")
    Response get(@Auth AuthenticatedUser authenticatedUser, @PathParam("id") int id) {
        Album result

        def h = this.dbi.open()
        try {
            def q = h.createQuery('''
                SELECT b.id, b.title, a.name as artist, b.edition, s.name as status,
                    to_char(b.released, 'YYYY-MM-DD') as released,
                    to_char(b.created, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') as created
                FROM mus_album b
                JOIN mus_status s ON b.status = s.id
                JOIN mus_artist a ON b.artist_id = a.id
                WHERE b.id = ?
            ''')
            q.bind(0, id)
            result = q.map(Album).first()
        } finally {
            h.close()
        }

        if (!result) {
            return this.notFound().build()
        }
        return this.ok(result).build()
    }
}
