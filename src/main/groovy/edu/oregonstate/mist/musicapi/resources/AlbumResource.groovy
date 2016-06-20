package edu.oregonstate.mist.musicapi.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.musicapi.core.Album
import io.dropwizard.auth.Auth
import java.util.regex.Pattern
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ResponseBuilder
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import org.skife.jdbi.v2.util.IntegerMapper

@Path('/album')
@groovy.transform.TypeChecked
class AlbumResource extends Resource {
    private DBI dbi
    private Pattern releaseDatePattern = Pattern.compile(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/) // YYYY-MM-DD

    public AlbumResource(DBI dbi) {
        this.dbi = dbi
    }

    /**
     * Responds to GET requests by returning an album
     *
     * @return album
     */
    @GET
    @Path('{id}')
    @Produces("application/json")
    Response getAlbum(@Auth AuthenticatedUser authenticatedUser, @PathParam("id") int id) {
        def h = this.dbi.open()
        try {
            def result = this.getAlbumById(h, id)
            if (result == null) {
                return this.notFound().build()
            }
            return this.ok(result).build()
        } finally {
            h.close()
        }
    }

    /**
     * Creates a new album
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    Response createAlbum(@Auth AuthenticatedUser _, Album album) {
        def h = this.dbi.open()
        try {
            // Get artist id
            def artistId = this.getOrCreateArtist(h, album.artist)

            // Get status id
            def statusId = this.getStatus(h, album.status)
            if (statusId == null) {
                return this.badRequest('invalid status').build()
            }

            // validate release date
            if (album.released != null) {
                if (!releaseDatePattern.matcher(album.released).matches()) {
                    return this.badRequest('invalid release date').build()
                }
            }

            def q = h.createStatement('''
                INSERT INTO mus_album (id, title, artist_id, edition, status, released, created)
                VALUES (
                    NULL, :title, :artist_id, :edition, :status_id,
                    NULL, cast(SYSTIMESTAMP at time zone 'UTC' as date))
            ''')
            q.bind('title', album.title)
            q.bind('artist_id', artistId.intValue())
            q.bind('edition', album.edition)
            q.bind('status_id', statusId.intValue())

            def gk = q.executeAndReturnGeneratedKeys()
            def rowid = gk.first().get('rowid')

            // Set release date, if provided
            if (album.released) {
                q = h.createStatement('''
                    UPDATE mus_album
                    SET released = to_date(:released, 'YYYY-MM-DD')
                    WHERE rowid = :rowid''')
                q.bind('released', album.released)
                q.bind('rowid', rowid)
                q.execute()
            }

            album = this.getAlbumByRowid(h, rowid)
            return this.ok(album).build()
        } finally {
            h.close()
        }
    }

    private Integer getStatus(Handle h, String status) {
        if (status == null || status == "") {
            return 1
        }
        def q = h.createQuery('SELECT id FROM mus_status WHERE name = ?')
        q.bind(0, status)
        return q.map(IntegerMapper.FIRST).first()
    }

    private Integer getOrCreateArtist(Handle h, String name) {
        if (name == null || name == "") {
            return null
        }

        // Does the artist already exist?
        def q = h.createQuery('SELECT id FROM mus_artist WHERE name = ?')
        q.bind(0, name)
        def id = q.map(IntegerMapper.FIRST).first()
        if (id != null) {
            return id
        }

        // Create a new row for the artist
        q = h.createStatement('''
            INSERT INTO mus_artist (id, name, created)
            VALUES (NULL, ?, cast(SYSTIMESTAMP at time zone 'UTC' as date))
        ''')
        q.bind(0, name)
        def gk = q.executeAndReturnGeneratedKeys()
        def rowid = gk.first().get('rowid')

        q = h.createQuery('SELECT id FROM mus_artist WHERE rowid = ?')
        q.bind(0, rowid)
        return q.map(IntegerMapper.FIRST).first()
    }

    private Album getAlbumById(Handle h, Integer id) {
        def q = h.createQuery('''
            SELECT b.id, b.title, a.name as artist, b.edition, s.name as status,
                to_char(b.released, 'YYYY-MM-DD') as released,
                to_char(b.created, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') as created
            FROM mus_album b
            JOIN mus_status s ON b.status = s.id
            JOIN mus_artist a ON b.artist_id = a.id
            WHERE b.id = ?
        ''')
        q.bind(0, id.intValue())
        def result = q.map(Album).first()
        if (result.title == null) { result.title = "" }
        if (result.artist == null) { result.artist = "" }
        if (result.edition == null) { result.edition = "" }
        return result
    }

    private Album getAlbumByRowid(Handle h, Object rowid) {
        def q = h.createQuery('''
            SELECT b.id, b.title, a.name as artist, b.edition, s.name as status,
                to_char(b.released, 'YYYY-MM-DD') as released,
                to_char(b.created, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') as created
            FROM mus_album b
            JOIN mus_status s ON b.status = s.id
            JOIN mus_artist a ON b.artist_id = a.id
            WHERE b.rowid = ?
        ''')
        q.bind(0, rowid)
        def result = q.map(Album).first()
        if (result.title == null) { result.title = "" }
        if (result.artist == null) { result.artist = "" }
        if (result.edition == null) { result.edition = "" }
        return result
    }
}
