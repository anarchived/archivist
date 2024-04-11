package org.crayne.archivist.test;

import org.crayne.archivist.index.blob.Blob;
import org.crayne.archivist.index.blob.BlobDimension;
import org.crayne.archivist.index.blob.BlobField;
import org.crayne.archivist.index.blob.region.Dimension;
import org.crayne.archivist.index.blob.region.Region;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Set;

public class BlobTest {

    @Test
    public void blobMergeTest() {
        final Region region1 = new Region(2, 3, Dimension.OVERWORLD, Path.of("region1"));
        final Region region2 = new Region(2, 4, Dimension.OVERWORLD, Path.of("region2"));
        final Blob blob1 = new Blob(Set.of(region1, region2), Dimension.OVERWORLD);

        final Region region3 = new Region(2, 4, Dimension.OVERWORLD, Path.of("region3"));
        final Blob blob2 = new Blob(Set.of(region3), Dimension.OVERWORLD);

        final Region region4 = new Region(-2, 1, Dimension.NETHER, Path.of("region4"));
        final Blob blob3 = new Blob(Set.of(region4), Dimension.NETHER);

        final Region region5 = new Region(-2, 1, Dimension.NETHER, Path.of("region5"));
        final Region region6 = new Region(-2, 2, Dimension.NETHER, Path.of("region6"));
        final Blob blob4 = new Blob(Set.of(region5, region6), Dimension.NETHER);

        final BlobField blobField = new BlobField("test");
        blobField.merge(blob1);
        blobField.merge(blob2);
        blobField.merge(blob3);
        blobField.merge(blob4);

        final BlobDimension overworldBlobDim = blobField.blobDimensions().get(Dimension.OVERWORLD);
        final BlobDimension netherBlobDim = blobField.blobDimensions().get(Dimension.NETHER);

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
