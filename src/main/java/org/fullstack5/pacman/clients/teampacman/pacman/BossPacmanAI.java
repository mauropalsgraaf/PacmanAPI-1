package org.fullstack5.pacman.clients.teampacman.pacman;

import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Maze;
import org.fullstack5.pacman.api.models.Piece;
import org.fullstack5.pacman.api.models.request.MoveRequest;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.clients.teampacman.AI;
import org.fullstack5.pacman.clients.teampacman.ServerComm;

import java.util.ArrayList;
import java.util.List;

public class BossPacmanAI implements AI {

    private final String gameId;
    private final String authId;
    private final Maze maze;
    private List<Direction> actions = new ArrayList<>();

    public BossPacmanAI(String gameId, String authId, Maze maze) {
        this.gameId = gameId;
        this.authId = authId;
        this.maze = maze;

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.WEST);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.EAST);
        actions.add(Direction.EAST);

        actions.add(Direction.EAST);

        actions.add(Direction.SOUTH);
        actions.add(Direction.SOUTH);

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);


        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.EAST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.WEST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.WEST);
        actions.add(Direction.WEST);
        actions.add(Direction.WEST);

        actions.add(Direction.NORTH);
        actions.add(Direction.NORTH);

        actions.add(Direction.NORTH);

        actions.add(Direction.SOUTH);

        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
        actions.add(Direction.EAST);
    }

    @Override
    public final void runAI(final GameState state) {

        Direction direction = this.actions.get(0);

        actions = actions.subList(1, actions.size());

        performMove(direction);
    }

    private void performMove(final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, Piece.Type.PACMAN);
        ServerComm.performMove(request);
    }
}
