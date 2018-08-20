package org.fullstack5.pacman.api.models;

import lombok.*;

/**
 * This class is meant to be immutable.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public final class Position {
    private int x;
    private int y;

    @Override
    public final String toString() {
        return String.format("[%d, %d]", x, y);
    }
}
