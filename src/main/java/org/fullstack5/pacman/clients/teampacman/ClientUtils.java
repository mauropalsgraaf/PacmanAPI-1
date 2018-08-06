package org.fullstack5.pacman.clients.teampacman;

import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Maze;
import org.fullstack5.pacman.api.models.Piece;
import org.fullstack5.pacman.api.models.Position;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.api.models.response.MovingPiece;
import org.fullstack5.pacman.clients.teampacman.models.WeightedPosition;

import java.util.*;

/**
 * General utils available in the client.
 */
public final class ClientUtils {

    private ClientUtils() {}

    public static <T> T randomItem(final List<T> items) {
        return items.get((int) (Math.random() * items.size()));
    }

    public static Direction getReverseDirection(final Direction direction) {
        switch(direction) {
        case NORTH:
            return Direction.SOUTH;
        case EAST:
            return Direction.WEST;
        case SOUTH:
            return Direction.NORTH;
        case WEST:
            return Direction.EAST;
        default:
            return null;
        }
    }

    public static Direction shortestDisDot(MovingPiece pacman, Maze maze, List<Position> dotsAndPallets) {

        Map<Direction, Position> availableDirections = getAvailableDirectionsAsMap(maze, pacman);

        return availableDirections.entrySet().stream()
                .filter(position -> isDotOrPallet(dotsAndPallets, position.getValue()))
                .findFirst()
                .orElse(availableDirections.entrySet().stream().findFirst().get()).getKey();
    }

    public static Map<Direction, Position> getAvailableDirectionsAsMap(final Maze maze, final MovingPiece piece) {
        final Map<Direction, Position> result = new HashMap<>();
        final int x = piece.getCurrentPosition().getX();
        final int y = piece.getCurrentPosition().getY();

        Position pos = new Position(x, y - 1);
        if (y == 0 || !isWall(pos, maze)) {
            result.put(Direction.NORTH, new Position(x, y - 1));
        }

        pos = new Position(x + 1, y);
        if (x == maze.getWidth() - 1 || !isWall(pos, maze)) {
            result.put(Direction.EAST, pos);
        }

        pos = new Position(x, y + 1);
        if (y == maze.getHeight() - 1 || !isWall(pos, maze)) {
            result.put(Direction.SOUTH, pos);
        }

        pos = new Position(x - 1, y);
        if (x == 0 || !maze.isWall(pos)) {
            result.put(Direction.WEST, pos);
        }
        return result;
    }

    public static boolean isWall(Position position, Maze maze) {
        return maze.isWall(position.getX(), position.getY());
    }


    public static boolean isDotOrPallet(List<Position> dotsPallets, Position position) {
        System.out.println("remaining dots:  "+ dotsPallets.size());
        return dotsPallets.contains(position);
    }

    /**
     * Get all free locations adjacent to the current location.
     */
    public static List<Direction> getAvailableDirections(final Maze maze, final MovingPiece piece) {
        final List<Direction> result = new ArrayList<>();
        final int x = piece.getCurrentPosition().getX();
        final int y = piece.getCurrentPosition().getY();
        if (y == 0 || !maze.isWall(x, y - 1)) {
            result.add(Direction.NORTH);
        }
        if (x == maze.getWidth() - 1 || !maze.isWall(x + 1, y)) {
            result.add(Direction.EAST);
        }
        if (y == maze.getHeight() - 1 || !maze.isWall(x, y + 1)) {
            result.add(Direction.SOUTH);
        }
        if (x == 0 || !maze.isWall(x - 1, y)) {
            result.add(Direction.WEST);
        }
        return result;
    }

    public static Piece.Type getGhostType(final int ghostNumber) {
        switch(ghostNumber) {
        case 0:
            return Piece.Type.BLINKY;
        case 1:
            return Piece.Type.PINKY;
        case 2:
            return Piece.Type.INKY;
        case 3:
            return Piece.Type.CLYDE;
        default:
            throw new IllegalArgumentException("No ghost with number " + ghostNumber);
        }
    }

    public static MovingPiece getGhost(final GameState state, final int ghostNumber) {
        switch(ghostNumber) {
        case 0:
            return state.getBlinky();
        case 1:
            return state.getPinky();
        case 2:
            return state.getInky();
        case 3:
            return state.getClyde();
        default:
            throw new IllegalArgumentException("No ghost with number " + ghostNumber);
        }
    }

    public static Position getPosition(final Maze maze, final Position current, final Direction direction) {
        final int x = (current.getX() + direction.getDeltaX() + maze.getWidth()) % maze.getWidth();
        final int y = (current.getY() + direction.getDeltaY() + maze.getHeight()) % maze.getHeight();
        return new Position(x, y);
    }

    public static <T> List<T> randomize(final T[] values) {
        return randomize(new ArrayList<T>(Arrays.asList(values)));
    }

    public static <T> List<T> randomize(final List<T> values) {
        final List<T> result = new ArrayList<>();
        while (!values.isEmpty()) {
            final T item = randomItem(values);
            result.add(item);
            values.remove(item);
        }
        return result;
    }

    public static <T> void dumpSet(final TreeSet<T> set) {
        System.out.println("Dump of set (" + set.size() + ")");
        for (final T object : set) {
            System.out.println(" " + object);
        }
    }
}
