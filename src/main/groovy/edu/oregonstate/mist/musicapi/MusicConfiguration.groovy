package edu.oregonstate.mist.musicapi

import edu.oregonstate.mist.api.Configuration
import io.dropwizard.db.DataSourceFactory

import javax.validation.Valid
import javax.validation.constraints.NotNull

class MusicConfiguration extends Configuration {
    @NotNull
    @Valid
    DataSourceFactory database = new DataSourceFactory()
}
