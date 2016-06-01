package edu.oregonstate.mist.musicapi.core

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A shelf
 */
class Shelf {
    Integer id
    String name

    @JsonProperty("album_urls")
    List<String> albumUrls
}
