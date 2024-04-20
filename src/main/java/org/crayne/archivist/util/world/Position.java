package org.crayne.archivist.util.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Position(double x, double y, double z) {

    @NotNull
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @NotNull
    public static Optional<Position> of(@NotNull final String asString) {
        final String[] split = asString.replace(",", " ").split(" ");
        if (split.length != 3) return Optional.empty();

        try {
            final double x = Double.parseDouble(split[0].trim());
            final double y = Double.parseDouble(split[1].trim());
            final double z = Double.parseDouble(split[2].trim());
            return Optional.of(new Position(x, y, z));
        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public Location toLocation(@NotNull final World world) {
        return new Location(world, x, y, z);
    }

}