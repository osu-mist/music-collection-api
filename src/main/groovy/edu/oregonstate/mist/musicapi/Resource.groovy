package edu.oregonstate.mist.musicapi

import java.util.regex.Pattern

/**
 * Base class for resources with some shared constants
 */
class Resource extends edu.oregonstate.mist.api.Resource {
    protected Pattern releaseDatePattern = Pattern.compile(/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/) // YYYY-MM-DD
    protected String releaseDateFormat = 'YYYY-MM-DD'
    protected String createdDateFormat = 'YYYY-MM-DD"T"HH24:MI:SS"Z"'
}
