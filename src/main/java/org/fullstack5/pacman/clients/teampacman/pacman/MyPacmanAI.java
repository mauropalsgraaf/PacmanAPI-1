package org.fullstack5.pacman.clients.teampacman.pacman;

import lombok.ToString;
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
import java.util.stream.Collectors;

public final class MyPacmanAI implements AI {

    private final String gameId;
    private final String authId;
    private final Maze maze;
    private Direction prev;

    public MyPacmanAI(String gameId, String authId, Maze maze) {
        this.gameId = gameId;
        this.authId = authId;
        this.maze = maze;
    }

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

            return new Pair<>(pos, Math.abs(pacmanX - pos.getX()) + Math.abs((pacmanY - pos.getY())));
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
        System.out.println(incentive1);

        DirectionsWithIncentive incentive2 = random(possibleDirections);
        System.out.println(incentive2);

        DirectionsWithIncentive incentive3 = new DirectionsWithIncentive(0, 0, 0, 0);
        if (prev != null && possibleDirections.contains(prev)) {
            incentive3 = new DirectionsWithIncentive(60, 60, 60, 60, Collections.singletonList(prev));
        }
        System.out.println(incentive3);

        DirectionsWithIncentive incentive4 = positionsOfGhosts(state.getPacman().getCurrentPosition(), Arrays.asList(blinky, clyde, inky, pinky), possibleDirections);
        System.out.println(incentive4);


        DirectionsWithIncentive finalIncentive = incentive1.mappend(incentive3).mappend(incentive4);
        System.out.println("final:" + finalIncentive.toString());
        Direction choice = finalIncentive.bestDirection(possibleDirections);

        prev = choice;

        performMove(choice);
    }

    private DirectionsWithIncentive random(List<Direction> possibleDirections) {
        int west = (int) (Math.random() * 20);
        int east = (int) (Math.random() * 20);
        int north = (int) (Math.random() * 20);
        int south = (int) (Math.random() * 20);

        return new DirectionsWithIncentive(north, east, south, west, possibleDirections);
    }

    private DirectionsWithIncentive positionsOfGhosts(Position position, List<MovingPiece> ghosts, List<Direction> possibleDirections) {
        List<Direction> res = ghosts.stream()
                .map(ghost -> new Pair<>(ghost.getCurrentPosition(), diffBetweenPositions(position, ghost.getCurrentPosition())))
                .filter(x -> x.getRight() <= 5)
                .map(x -> {
                    Position ghostPos = x.getLeft();
                    if (Math.abs(ghostPos.getX() - position.getX()) >= Math.abs(ghostPos.getY() - position.getY()) && ghostPos.getX() < position.getX()) {
                        return Direction.WEST;
                    }

                    if (Math.abs(ghostPos.getX() - position.getX()) >= Math.abs(ghostPos.getY() - position.getY()) && ghostPos.getX() > position.getX()) {
                        return Direction.EAST;
                    }

                    if (Math.abs(ghostPos.getX() - position.getX()) <= Math.abs(ghostPos.getY() - position.getY()) && ghostPos.getY() < position.getY()) {
                        return Direction.NORTH;
                    }

                    if (Math.abs(ghostPos.getX() - position.getX()) <= Math.abs(ghostPos.getY() - position.getY()) && ghostPos.getX() > position.getX()) {
                        return Direction.SOUTH;
                    }

                    return null; // cannot happen
                })
                .collect(Collectors.toList());

        DirectionsWithIncentive incentive1 = new DirectionsWithIncentive(0, 0, 0, 0);
        DirectionsWithIncentive incentive2 = new DirectionsWithIncentive(0, 0, 0, 0);
        DirectionsWithIncentive incentive3= new DirectionsWithIncentive(0, 0, 0, 0);
        DirectionsWithIncentive incentive4 = new DirectionsWithIncentive(0, 0, 0, 0);
        if (res.contains(Direction.EAST)) {
            incentive1 = new DirectionsWithIncentive(50, -50, 50, 50, possibleDirections);
        }

        if (res.contains(Direction.WEST)) {
            incentive2 = new DirectionsWithIncentive(50, 50, 50, -50, possibleDirections);
        }

        if (res.contains(Direction.SOUTH)) {
            incentive3 = new DirectionsWithIncentive(50, 50, -50, 50, possibleDirections);
        }

        if (res.contains(Direction.NORTH)) {
            incentive4 = new DirectionsWithIncentive(-50, 50, 50, 50, possibleDirections);
        }

        return incentive1.mappend(incentive2).mappend(incentive3).mappend(incentive4);
    }

    private Integer diffBetweenPositions(Position pos1, Position pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY());
    }

    private DirectionsWithIncentive closestWithWeights(Pair<Position, Integer> pair, MovingPiece pacman, List<Direction> possibleDirections) {
        Integer x = pair.getLeft().getX();
        Integer y = pair.getLeft().getY();

        // if the dot is horizontally closer than vertically
        if (Math.abs(x - pacman.getCurrentPosition().getX()) < Math.abs(y - pacman.getCurrentPosition().getY())) {
            if (possibleDirections.contains(Direction.EAST) && pacman.getCurrentPosition().getX() < x) {
                return new DirectionsWithIncentive(0,30, 0, 0);
            } else if (possibleDirections.contains(Direction.WEST)) {
                return new DirectionsWithIncentive(0,0, 0, 30);
            } else if (possibleDirections.contains(Direction.SOUTH) && pacman.getCurrentPosition().getY() < y) {
                return new DirectionsWithIncentive(0,0, 30, 0);
            } else {
                return new DirectionsWithIncentive(30,0, 0, 0);
            }
        } else {
            if (possibleDirections.contains(Direction.SOUTH) && pacman.getCurrentPosition().getY() < y) { // This could be reversed, because i'm not sure if the y is top to bottom (where 1 is top)
                return new DirectionsWithIncentive(0, 0, 30, 0);
            } else if (possibleDirections.contains(Direction.NORTH)){
                return new DirectionsWithIncentive(30,0, 0, 0);
            } else if (possibleDirections.contains(Direction.EAST) && pacman.getCurrentPosition().getX() < x) {
                return new DirectionsWithIncentive(0, 30, 0, 0);
            } else {
                return new DirectionsWithIncentive(0, 0, 0, 30);
            }
        }
    }

    private void performMove(final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, Piece.Type.PACMAN);
        ServerComm.performMove(request);
    }

    @ToString
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

        public DirectionsWithIncentive(int north, int east, int south, int west, List<Direction> possibleDirections) {
            if (possibleDirections.contains(Direction.EAST)) {
                this.east = east;
            }

            if (possibleDirections.contains(Direction.WEST)) {
                this.west = west;
            }

            if (possibleDirections.contains(Direction.NORTH)) {
                this.north = north;
            }

            if (possibleDirections.contains(Direction.SOUTH)) {
                this.south = south;
            }
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
