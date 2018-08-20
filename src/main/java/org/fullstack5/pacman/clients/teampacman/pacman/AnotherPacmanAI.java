package org.fullstack5.pacman.clients.teampacman.pacman;

import lombok.Builder;
import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Maze;
import org.fullstack5.pacman.api.models.Piece;
import org.fullstack5.pacman.api.models.Position;
import org.fullstack5.pacman.api.models.request.MoveRequest;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.api.models.response.MovingPiece;
import org.fullstack5.pacman.clients.teampacman.AI;
import org.fullstack5.pacman.clients.teampacman.ClientUtils;
import org.fullstack5.pacman.clients.teampacman.ServerComm;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnotherPacmanAI implements AI {

    private final String gameId;
    private final String authId;
    private final Maze maze;
    private Integer turn = 1;


    public AnotherPacmanAI(String gameId, String authId, Maze maze) {
        this.gameId = gameId;
        this.authId = authId;
        this.maze = maze;
    }

    @Override
    public void runAI(GameState gameState) {
        System.out.println("turn = " + turn);
        turn = turn + 1;
        State state = new State();

        state.setWalls(Arrays.copyOf(maze.getWalls(), maze.getWalls().length));


        List<MovingPiece> ghosts = new ArrayList<>();
        ghosts.add(gameState.getBlinky());
        ghosts.add(gameState.getClyde());
        ghosts.add(gameState.getPinky());
        ghosts.add(gameState.getInky());

        state.setEdges(
                ghosts.stream()
                        .map(MovingPiece::getCurrentPosition)
                        .collect(Collectors.toList())
        );

        state.setPacman(gameState.getPacman().getCurrentPosition());

        List<State> states = new ArrayList<>();
        states.add(state);

        List<State> possibleStates = states.stream().flatMap(
                s -> {
                    Supplier<Stream<State>> streamSupplier = () -> this.method(s).stream();

//                    streamSupplier.get().forEach(System.out::println);
                    return streamSupplier.get();
                }
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).flatMap(
                s -> this.method(s).stream()
        ).collect(Collectors.toList());

//        possibleStates.stream()
//        .map(State::getOriginalDirection)
//        .forEach(System.out::println);

        Map<Direction, List<State>> result = possibleStates.stream().collect(Collectors.groupingBy(State::getOriginalDirection));

        Direction direction = null;
        Integer nrOfStates = 0;

        for (Map.Entry<Direction, List<State>> entry : result.entrySet())
        {
            if (direction == null) {
//                System.out.println(entry.getValue().size());
//                System.out.println("isNUll");
//                System.out.println(entry.getKey());
                direction = entry.getKey();
                nrOfStates = entry.getValue().size();
            } else if (entry.getValue().size() >= nrOfStates) {
//                System.out.println(entry.getValue().size());
//                System.out.println(entry.getKey());
                direction = entry.getKey();
                nrOfStates = entry.getValue().size();
            }
        }


        if (direction == null) {
            direction = Direction.WEST;
        }

        System.out.println("What is the direction");
        System.out.println(direction);

        performMove(direction);
    }

    private List<State> method(State state) {
        state = state.toBuilder().build();
//        state.edges.forEach(System.out::println);
        List<Position> newEdges = state.edges.stream().flatMap(
                e -> {
                    List<Direction> directions = ClientUtils.getAvailableDirections(maze, e);

                    return directions.stream().map(
                            d -> new Position(e.getX() + d.getDeltaX(), e.getY() + d.getDeltaY())
                    );
                }
        ).collect(Collectors.toList());

        state.turnToStone(state.edges);
        state.setEdges(newEdges);
        state.turnToStone(state.pacman);

        List<Direction> directions = ClientUtils.getAvailableDirections(state, state.pacman);

        List<State> newStates = new ArrayList<>();

        for (int i = 0; i < directions.size(); i++) {
            Direction d = directions.get(i);

            State newState = state.toBuilder().build();

            newState.setPacman(
                new Position(state.pacman.getX() + d.getDeltaX(), state.pacman.getY() + d.getDeltaY())
            );

            if (newState.getOriginalDirection() == null) {
                newState.setOriginalDirection(d);
            }

            newStates.add(newState);
        }

        return newStates;
    }

    private void performMove(final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, Piece.Type.PACMAN);

        ServerComm.performMove(request);
    }
}
