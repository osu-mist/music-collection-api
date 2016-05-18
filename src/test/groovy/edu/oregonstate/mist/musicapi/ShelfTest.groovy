package edu.oregonstate.mist.musicapi

import edu.oregonstate.mist.musicapi.core.Shelf
import org.junit.Test
import org.junit.Assert

class ShelfTest {
    @Test
    public void testShelf() {
        Shelf s = new Shelf()
        Assert.assertEquals(s.id, null)
        Assert.assertEquals(s.name, null)
    }
}
