package edu.oregonstate.mist.musicapi.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.musicapi.core.Album
import io.dropwizard.auth.Auth
import java.util.regex.Pattern
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.DELETE
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
    private String releaseDateFormat = 'YYYY-MM-DD'
    private String createdDateFormat = 'YYYY-MM-DD"T"HH24:MI:SS"Z"'

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
            // validate release date
            if (album.released != null) {
                if (!releaseDatePattern.matcher(album.released).matches()) {
                    return this.badRequest('invalid release date').build()
                }
            }

            // Get status id
            def statusId = this.getStatus(h, album.status)
            if (statusId == null) {
                return this.badRequest('invalid status').build()
            }

            // Get artist id
            def artistId = this.getOrCreateArtist(h, album.artist)

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
                    SET released = to_date(:released, :format)
                    WHERE rowid = :rowid''')
                q.bind('released', album.released)
                q.bind('format', this.releaseDateFormat)
                q.bind('rowid', rowid)
                q.execute()
            }

            album = this.getAlbumByRowid(h, rowid)
            return this.ok(album).build()
        } finally {
            h.close()
        }
    }

    @PUT
    @Path("{id}")
    @Consumes('application/json')
    @Produces('application/json')
    Response updateAlbum(@Auth AuthenticatedUser _, @PathParam("id") int id, Album newAlbum) {
        def h = this.dbi.open()
        try {
            if (newAlbum.id != null && newAlbum.id != id) {
                return this.badRequest('id property does not match url').build()
            }

            // Check if album exists
            def oldAlbum = this.getAlbumById(h, id)
            if (oldAlbum == null) {
                return this.notFound().build()
            }

            // validate release date
            if (newAlbum.released != null) {
                if (!releaseDatePattern.matcher(newAlbum.released).matches()) {
                    return this.badRequest('invalid release date').build()
                }
            }

            // Get status id
            def statusId = this.getStatus(h, newAlbum.status)
            if (statusId == null) {
                return this.badRequest('invalid status').build()
            }

            // Get artist id
            def artistId = this.getOrCreateArtist(h, newAlbum.artist)

            // Do the easy stuff
            def q = h.createStatement('''
                UPDATE mus_album
                SET title = :title,
                    edition = :edition,
                    status = :status,
                    artist_id = :artist_id
                WHERE id = :id
            ''')
            q.bind("id", id)
            q.bind("title", newAlbum.title)
            q.bind("edition", newAlbum.edition)
            q.bind("status", statusId.intValue())
            q.bind("artist_id", artistId.intValue())
            q.execute()

            // Set or clear the release date
            if (newAlbum.released) {
                q = h.createStatement('''UPDATE mus_album SET released = to_date(:released, :format) WHERE id = :id''')
                q.bind("id", id)
                q.bind("released", newAlbum.released)
                q.bind("format", this.releaseDateFormat)
                q.execute()
            } else {
                q = h.createStatement('''UPDATE mus_album SET released = NULL WHERE id = :id''')
                q.bind("id", id)
                q.execute()
            }

            def album = this.getAlbumById(h, id)
            return this.ok(album).build()
        } finally {
            h.close()
        }
    }

    @DELETE
    @Path("{id}")
    @Produces('application/json')
    Response deleteAlbum(@Auth AuthenticatedUser _, @PathParam("id") int id) {
        def h = this.dbi.open()
        try {
            def q = h.createQuery('SELECT id FROM mus_album WHERE id = ?')
            if (q.bind(0, id).first() == null) {
                return this.notFound().build()
            }
            h.execute('DELETE FROM mus_shelf_album_map WHERE album_id = ?', id)
            h.execute('DELETE FROM mus_album WHERE id = ?', id)
            return Response.noContent().build()
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
                to_char(b.released, :released_format) as released,
                to_char(b.created, :created_format) as created
            FROM mus_album b
            JOIN mus_status s ON b.status = s.id
            LEFT JOIN mus_artist a ON b.artist_id = a.id
            WHERE b.id = :id
        ''')
        q.bind("id", id.intValue())
        q.bind("released_format", this.releaseDateFormat)
        q.bind("created_format", this.createdDateFormat)
        def result = q.map(Album).first()
        if (result.title == null) { result.title = "" }
        if (result.artist == null) { result.artist = "" }
        if (result.edition == null) { result.edition = "" }
        return result
    }

    private Album getAlbumByRowid(Handle h, Object rowid) {
        def q = h.createQuery('''
            SELECT b.id, b.title, a.name as artist, b.edition, s.name as status,
                to_char(b.released, :released_format) as released,
                to_char(b.created, :created_format) as created
            FROM mus_album b
            JOIN mus_status s ON b.status = s.id
            LEFT JOIN mus_artist a ON b.artist_id = a.id
            WHERE b.rowid = :rowid
        ''')
        q.bind("rowid", rowid)
        q.bind("released_format", this.releaseDateFormat)
        q.bind("created_format", this.createdDateFormat)
        def result = q.map(Album).first()
        if (result.title == null) { result.title = "" }
        if (result.artist == null) { result.artist = "" }
        if (result.edition == null) { result.edition = "" }
        return result
    }
}
