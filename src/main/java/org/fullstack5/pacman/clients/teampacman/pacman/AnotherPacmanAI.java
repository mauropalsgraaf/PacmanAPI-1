package org.fullstack5.pacman.clients.teampacman.pacman;

import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Maze;
import org.fullstack5.pacman.api.models.Piece;
import org.fullstack5.pacman.api.models.request.MoveRequest;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.clients.teampacman.AI;
import org.fullstack5.pacman.clients.teampacman.ServerComm;

public class AnotherPacmanAI implements AI {

    private final String gameId;
    private final String authId;
    private final Maze maze;

    public AnotherPacmanAI(String gameId, String authId, Maze maze) {
        this.gameId = gameId;
        this.authId = authId;
        this.maze = maze;
    }

        @Override
    public void runAI(GameState gameState) {
        Direction choice = null;
    }

    private void performMove(final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, Piece.Type.PACMAN);
        ServerComm.performMove(request);
    }


}
