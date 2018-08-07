package org.fullstack5.pacman.clients.teampacman.pacman;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

        List<MovingPiece> ghosts = new ArrayList<>();
        ghosts.add(gameState.getBlinky());
        ghosts.add(gameState.getClyde());
        ghosts.add(gameState.getPinky());
        ghosts.add(gameState.getInky());

        List<Maze> mazes = possibleGhostPositions(maze, ghosts);

        List<Direction> directions = ClientUtils.getAvailableDirections(maze, gameState.getPacman());

        Direction choice = null;


    }

    private void performMove(final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, Piece.Type.PACMAN);


        ServerComm.performMove(request);
    }

    private List<Maze> possibleGhostPositions(Maze maze, List<MovingPiece> ghost) {
        List<Direction> directions = ClientUtils.getAvailableDirections(maze, ghost);

        List<Maze> mazes = new ArrayList<>();
        Maze newMaze = maze.toBuilder().build();
        newMaze.turnToStone(ghost.getCurrentPosition());
        directions.forEach(d -> {
            Maze. toAdd = new Position(ghost.getCurrentPosition().getX() + d.getDeltaX(), ghost.getCurrentPosition().getY() + d.getDeltaY());
            positions.add(toAdd);
        });

        return positions;
    }

}
