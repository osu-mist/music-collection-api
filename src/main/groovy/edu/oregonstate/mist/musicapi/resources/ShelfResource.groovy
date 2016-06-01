package edu.oregonstate.mist.musicapi.resources

import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.musicapi.core.Album
import edu.oregonstate.mist.musicapi.core.Shelf
import edu.oregonstate.mist.api.AuthenticatedUser
import io.dropwizard.auth.Auth
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ResponseBuilder
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

import java.sql.ResultSet
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.util.IntegerMapper
import org.skife.jdbi.v2.tweak.ResultSetMapper

/**
 * Sample resource class.
 */
@Path('/shelf/{id}')
class ShelfResource extends Resource {
    private DBI dbi

    @Context
    private UriInfo uriInfo

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
    Response getShelf(@Auth AuthenticatedUser _, @PathParam("id") int id) {
        def h = this.dbi.open()
        try {
            Shelf result

            def q = h.createQuery('SELECT id, name FROM MUS_SHELF WHERE id = ?')
            q.bind(0, id)
            result = q.map(Shelf).first()

            def ub = uriInfo.getBaseUriBuilder().path(AlbumResource)
            q = h.createQuery('SELECT album_id as id FROM MUS_SHELF_ALBUM_MAP WHERE shelf_id = ?')
            q.bind(0, id)
            result.albumUrls = q.map(new AlbumUrlMapper(ub)).list()

            return this.ok(result).build()
        } finally {
            h.close()
        }
    }

    private class AlbumUrlMapper implements ResultSetMapper<String> {
        private UriBuilder ub
        AlbumUrlMapper(UriBuilder ub) {
            this.ub = ub
        }
        String map(int index, ResultSet r, StatementContext ctx) {
            return this.ub.build(r.getLong("id")).toString()
        }
    }

    @GET
    @Path('/albums')
    @Produces("application/json")
    Response getAlbums(@Auth AuthenticatedUser _, @PathParam("id") int id) {
        List<Album> result

        def h = this.dbi.open()
        try {
            def q = h.createQuery('''
                SELECT b.id, b.title, a.name as artist, b.edition, s.name as status,
                    to_char(b.released, 'YYYY-MM-DD') as released,
                    to_char(b.created, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') as created
                FROM mus_album b
                JOIN mus_status s ON b.status = s.id
                JOIN mus_artist a ON b.artist_id = a.id
                JOIN mus_shelf_album_map m ON m.album_id = b.id
                WHERE m.shelf_id = ?
            ''')
            q.bind(0, id)
            result = q.map(Album).list()
        } finally {
            h.close()
        }

        return this.ok(result).build()
    }
}
