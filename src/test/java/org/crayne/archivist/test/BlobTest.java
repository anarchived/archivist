package org.crayne.archivist.test;

import org.crayne.archivist.index.blob.Blob;
import org.crayne.archivist.index.blob.BlobField;
import org.crayne.archivist.index.blob.BlobWorld;
import org.crayne.archivist.index.blob.region.Region;
import org.crayne.archivist.index.blob.region.WorldType;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlobTest {

    @Test
    public void blobMergeTest() {
        final Region region1 = new Region(2, 3, WorldType.OVERWORLD, Path.of("base1/region1"));
        final Region region2 = new Region(2, 4, WorldType.OVERWORLD, Path.of("base1/region2"));
        final Blob blob1 = new Blob(Set.of(region1, region2), WorldType.OVERWORLD);

        final Region region3 = new Region(2, 4, WorldType.OVERWORLD, Path.of("base2/region3"));
        final Blob blob2 = new Blob(Set.of(region3), WorldType.OVERWORLD);

        final Region region4 = new Region(-2, 1, WorldType.NETHER, Path.of("base3/region4"));
        final Blob blob3 = new Blob(Set.of(region4), WorldType.NETHER);

        final Region region5 = new Region(-2, 1, WorldType.NETHER, Path.of("base4/region5"));
        final Region region6 = new Region(-2, 2, WorldType.NETHER, Path.of("base4/region6"));
        final Blob blob4 = new Blob(Set.of(region5, region6), WorldType.NETHER);

        final BlobField blobField = new BlobField("test");
        blobField.merge(blob1);
        blobField.merge(blob2);
        blobField.merge(blob3);
        blobField.merge(blob4);

        final BlobWorld overworldBlobDim = blobField.blobWorlds().get(WorldType.OVERWORLD);
        final BlobWorld netherBlobDim = blobField.blobWorlds().get(WorldType.NETHER);

        final Blob blob0Overworld = overworldBlobDim.blobs().get(0);
        final Blob blob1Overworld = overworldBlobDim.blobs().get(1);

        final Blob blob0Nether = netherBlobDim.blobs().get(0);
        final Blob blob1Nether = netherBlobDim.blobs().get(1);

        assertEquals(2, blob0Overworld.regions().size());
        assertEquals(1, blob1Overworld.regions().size());

        assertEquals(1, blob0Nether.regions().size());
        assertEquals(2, blob1Nether.regions().size());

        assertTrue(blob0Overworld.regions().contains(region1));
        assertTrue(blob0Overworld.regions().contains(region2));

        assertTrue(blob1Overworld.regions().contains(region3));

        assertTrue(blob0Nether.regions().contains(region4));

        assertTrue(blob1Nether.regions().contains(region5));
        assertTrue(blob1Nether.regions().contains(region6));
    }

}
