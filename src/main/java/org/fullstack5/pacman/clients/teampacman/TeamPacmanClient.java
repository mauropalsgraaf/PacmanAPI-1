package org.fullstack5.pacman.clients.teampacman;

import org.fullstack5.pacman.api.models.GhostsRunner;
import org.fullstack5.pacman.api.models.PacmanRunner;
import org.fullstack5.pacman.api.models.PlayerType;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.api.models.response.PlayerRegistered;
import org.fullstack5.pacman.clients.teampacman.ghosts.AStarGhostAI;
import org.fullstack5.pacman.clients.teampacman.ghosts.RandomGhostAI;
import org.fullstack5.pacman.clients.teampacman.pacman.AnotherPacmanAI;
import org.fullstack5.pacman.clients.teampacman.pacman.BossPacmanAI;
import org.fullstack5.pacman.clients.teampacman.pacman.MyPacmanAI;
import org.fullstack5.pacman.clients.teampacman.pacman.RandomPacmanAI;
import reactor.core.publisher.Flux;

import static org.fullstack5.pacman.api.models.PacmanRunner.MYIMPL;
import static org.fullstack5.pacman.api.models.PacmanRunner.RANDOM;

public final class TeamPacmanClient implements Runnable {
    private String gameId;
    private PacmanRunner pacmanRunner;
    private GhostsRunner ghostsRunner;

    public TeamPacmanClient(String gameId, PacmanRunner pacmanRunner) {
        this.gameId = gameId;
        this.pacmanRunner = pacmanRunner;
    }

    public TeamPacmanClient(String gameId, GhostsRunner ghostsRunner) {
        this.gameId = gameId;
        this.ghostsRunner = ghostsRunner;
    }

    public TeamPacmanClient(String gameId, PacmanRunner pacmanRunner, GhostsRunner ghostsRunner) {
        this.gameId = gameId;
        this.pacmanRunner = pacmanRunner;
        this.ghostsRunner = ghostsRunner;
    }

    @Override
    public final void run() {
        if (gameId == null) {
            gameId = ServerComm.startGame();
        }

        final Flux<GameState> flux = ServerComm.establishGameStateFlux(gameId);

        RunnerThread thread;
        if (pacmanRunner != null) {
            final PlayerRegistered player = ServerComm.registerPlayer(gameId, PlayerType.PACMAN);
            final AI pacmanAI;

            if (pacmanRunner == RANDOM) {
                pacmanAI = new AStarGhostAI(gameId, player.getAuthId(), player.getMaze());
            } else if (pacmanRunner == MYIMPL) {
                pacmanAI = new MyPacmanAI(gameId, player.getAuthId(), player.getMaze());
            } else {
                throw new IllegalArgumentException("Unknown pacman runner");
            }
            thread = new RunnerThread(pacmanAI);
            flux.subscribe(thread::updateState);
        }

        if (ghostsRunner != null) {
            final PlayerRegistered player = ServerComm.registerPlayer(gameId, PlayerType.GHOSTS);
            final AI ghostsAI;
            switch (ghostsRunner) {
                case RANDOM:
                    ghostsAI = new RandomGhostAI(gameId, player.getAuthId(), player.getMaze());
                    break;
                case ASTAR:
                    ghostsAI = new AStarGhostAI(gameId, player.getAuthId(), player.getMaze());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown ghosts runner");
            }
            thread = new RunnerThread(ghostsAI);
            flux.subscribe(thread::updateState);
        }
    }

    public static void main(final String...args) {
        new TeamPacmanClient(null, MYIMPL, GhostsRunner.ASTAR).run();
    }

}
