package edu.oregonstate.mist.musicapi

import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.InfoResource
import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.BasicAuthenticator
import edu.oregonstate.mist.api.NullHealthCheck
import edu.oregonstate.mist.musicapi.MusicConfiguration
import edu.oregonstate.mist.musicapi.resources.AlbumResource
import edu.oregonstate.mist.musicapi.resources.ShelfResource
import io.dropwizard.Application
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.auth.AuthFactory
import io.dropwizard.auth.basic.BasicAuthFactory

/**
 * Main application class.
 */
class MusicApplication extends Application<MusicConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    void run(MusicConfiguration configuration, Environment environment) {
        def factory = new DBIFactory()
        def dbi = factory.build(environment, configuration.database, "jdbi")

        Resource.loadProperties('resource.properties')
        environment.jersey().register(new InfoResource())
        environment.jersey().register(new AlbumResource(dbi))
        environment.jersey().register(new ShelfResource(dbi))
        environment.jersey().register(
                AuthFactory.binder(
                        new BasicAuthFactory<AuthenticatedUser>(
                                new BasicAuthenticator(configuration.getCredentialsList()),
                                'MusicApplication',
                                AuthenticatedUser.class)))
        // Shut the health check warning up
        environment.healthChecks().register("null", new NullHealthCheck())
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    static void main(String[] arguments) throws Exception {
        new MusicApplication().run(arguments)
    }
}
