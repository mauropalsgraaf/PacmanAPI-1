package org.fullstack5.pacman.clients.teampacman.pacman;

import lombok.AllArgsConstructor;
import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Maze;
import org.fullstack5.pacman.api.models.Piece;
import org.fullstack5.pacman.api.models.Position;
import org.fullstack5.pacman.api.models.request.MoveRequest;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.api.models.response.MovingPiece;
import org.fullstack5.pacman.clients.teampacman.AI;
import org.fullstack5.pacman.clients.teampacman.ClientUtils;
import org.fullstack5.pacman.clients.teampacman.Pair;
import org.fullstack5.pacman.clients.teampacman.ServerComm;

import java.util.*;

@AllArgsConstructor
public final class MyPacmanAI implements AI {

    private final String gameId;
    private final String authId;
    private final Maze maze;

    @Override
    public final void runAI(final GameState state) {
        MovingPiece blinky = state.getBlinky();
        MovingPiece clyde = state.getClyde();
        MovingPiece inky = state.getInky();
        MovingPiece pinky = state.getPinky();

        MovingPiece pacman = state.getPacman();

        Pair<Position, Integer> closest = state.getRemainingDots().stream().map(pos -> {
            int pacmanX = pacman.getCurrentPosition().getX();
            int pacmanY = pacman.getCurrentPosition().getY();

            return new Pair<Position, Integer>(pos, Math.abs(pacmanX - pos.getX()) + (pacmanY - pos.getY()));
        }).min((pair1, pair2) -> {
            if (pair1.getRight() < pair2.getRight()) {
                return -1;
            } else if (pair1.getRight() == pair2.getRight()) {
                return 0;
            } else {
                return 1;
            }
        }).get();

        List<Direction> possibleDirections = ClientUtils.getAvailableDirections(maze, pacman);

        DirectionsWithIncentive incentive1 = closestWithWeights(closest, pacman, possibleDirections);

        DirectionsWithIncentive incentive2 = random(possibleDirections);

        //DirectionsWithIncentive incentive3 = positionsOfGhosts(Arrays.asList(blinky, clyde, inky, pinky));

        Direction choice = incentive1.mappend(incentive2).bestDirection(possibleDirections);

        performMove(choice);
    }

    private DirectionsWithIncentive random(List<Direction> possibleDirections) {
        return new DirectionsWithIncentive(
                (int)(Math.random() * 70),
                (int)(Math.random() * 70),
                (int)(Math.random() * 70),
                (int)(Math.random() * 70)
        );
    }

//    private DirectionsWithIncentive positionsOfGhosts(Position position, List<MovingPiece> ghosts) {
//        ghosts.stream().map(
//                ghost -> {
//                    Integer x = ghost.getCurrentPosition().getX();
//                    Integer y = ghost.getCurrentPosition().getY();
//
//                    return Math.abs(position.getX() - x) + Math.abs(position.getY() - y);
//                }
//        ).reduce(
//                (x, y) -> x + y
//        );
//    }

    private DirectionsWithIncentive closestWithWeights(Pair<Position, Integer> pair, MovingPiece pacman, List<Direction> possibleDirections) {
        Integer x = pair.getLeft().getX();
        Integer y = pair.getLeft().getY();

        // if the dot is horizontally closer than vertically
        if (Math.abs(x - pacman.getCurrentPosition().getX()) < Math.abs(y - pacman.getCurrentPosition().getY())) {
            if (possibleDirections.contains(Direction.EAST) && pacman.getCurrentPosition().getX() < x) {
                return new DirectionsWithIncentive(0, 50, 0, 0);
            } else if (possibleDirections.contains(Direction.WEST)) {
                return new DirectionsWithIncentive(0, 0, 0, 50);
            } else if (possibleDirections.contains(Direction.SOUTH) && pacman.getCurrentPosition().getY() < y) {
                return new DirectionsWithIncentive(0, 0, 50, 0);
            } else {
                return new DirectionsWithIncentive(50, 0, 0, 0);
            }
        } else {
            if (possibleDirections.contains(Direction.SOUTH) && pacman.getCurrentPosition().getY() < y) { // This could be reversed, because i'm not sure if the y is top to bottom (where 1 is top)
                return new DirectionsWithIncentive(0, 0, 50, 0);
            } else if (possibleDirections.contains(Direction.NORTH)){
                return new DirectionsWithIncentive(50, 0, 0, 0);
            } else if (possibleDirections.contains(Direction.EAST) && pacman.getCurrentPosition().getX() < x) {
                return new DirectionsWithIncentive(0, 50, 0, 0);
            } else {
                return new DirectionsWithIncentive(0, 0, 0, 50);
            }
        }
    }

    private void performMove(final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, Piece.Type.PACMAN);
        ServerComm.performMove(request);
    }

    private class DirectionsWithIncentive {
        private int north;
        private int east;
        private int south;
        private int west;

        public DirectionsWithIncentive(int north, int east, int south, int west) {
            this.north = north;
            this.east = east;
            this.south = south;
            this.west = west;
        }

        public DirectionsWithIncentive mappend(DirectionsWithIncentive dwi) {
            return new DirectionsWithIncentive(
                this.north + dwi.north,
                this.east+ dwi.east,
                this.south + dwi.south,
                this.west + dwi.west
            );
        }

        public Direction bestDirection(List<Direction> possibleDirections) {
            if (north >= east && north >= south && north >= west) {
                if (possibleDirections.contains(Direction.NORTH)) {
                    return Direction.NORTH;
                }
            } else if (east >= south && east >= west) {
                if (possibleDirections.contains(Direction.EAST)) {
                    return Direction.EAST;
                }
            } else if (south >= west) {
                if (possibleDirections.contains(Direction.SOUTH)) {
                    return Direction.SOUTH;
                }
            }
            return Direction.WEST;
        }
    }
}
