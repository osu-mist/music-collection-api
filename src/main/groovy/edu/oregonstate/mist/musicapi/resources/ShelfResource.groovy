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
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.util.IntegerMapper

/**
 * Shelf resource class.
 */
@Path('/shelf')
class ShelfResource extends Resource {
    private DBI dbi

    @Context
    private UriInfo uriInfo

    public ShelfResource(DBI dbi) {
        this.dbi = dbi
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    Response createShelf(@Auth AuthenticatedUser _, @PathParam("id") int id, Shelf shelf) {
        def h = this.dbi.open()
        try {
            def q = h.createStatement("INSERT INTO MUS_SHELF (id, name, created) VALUES (NULL, ?, SYSDATE)")
            q.bind(0, shelf.name)
            def gk = q.executeAndReturnGeneratedKeys()
            def rowid = gk.first().get("rowid")

            q = h.createQuery("SELECT * FROM MUS_SHELF WHERE rowid = ?")
            q.bind(0, rowid)
            shelf = q.map(Shelf).first()
            shelf.albumUrls = []
            return this.created(shelf).build()
        } finally {
            h.close()
        }
    }

    /**
     * Responds to GET requests by returning a message.
     *
     * @return message
     */
    @GET
    @Path('{id}')
    @Produces("application/json")
    Response getShelf(@Auth AuthenticatedUser _, @PathParam("id") int id) {
        def h = this.dbi.open()
        try {
            // Get the shelf
            Shelf shelf
            def q = h.createQuery('''
                SELECT id, name,
                    to_char(created, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') as created
                FROM MUS_SHELF
                WHERE id = ?
            ''')
            q.bind(0, id)
            shelf = q.map(Shelf).first()
            if (shelf == null) {
                return this.notFound().build()
            }

            // Get album ids
            q = h.createQuery('SELECT album_id as id FROM MUS_SHELF_ALBUM_MAP WHERE shelf_id = ?')
            q.bind(0, id)

            def ub = uriInfo.getBaseUriBuilder().path(AlbumResource)
            shelf.albumUrls = []
            for (Integer albumId : q.map(IntegerMapper.FIRST)) {
                shelf.albumUrls.add(ub.build(albumId).toString())
            }

            return this.ok(shelf).build()
        } finally {
            h.close()
        }
    }

    @GET
    @Path('{id}/albums')
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

        // TODO: return notFound if shelf does not exist
        return this.ok(result).build()
    }
}
