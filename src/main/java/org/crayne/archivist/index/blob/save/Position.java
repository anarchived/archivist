package org.crayne.archivist.index.blob.save;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public final class Position {

    private final double x;
    private final double y;
    private final double z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

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

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Position) obj;
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(that.x) &&
                Double.doubleToLongBits(this.y) == Double.doubleToLongBits(that.y) &&
                Double.doubleToLongBits(this.z) == Double.doubleToLongBits(that.z);
    }

    public int hashCode() {
        return Objects.hash(x, y, z);
    }


}
