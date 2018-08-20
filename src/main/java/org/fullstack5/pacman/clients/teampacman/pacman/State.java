package org.fullstack5.pacman.clients.teampacman.pacman;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Position;

import java.util.List;

@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class State {
    public boolean[][] walls;
    public List<Position> edges;

    public Position pacman;

    private Direction originalDirection;

    public State() {
    }

    public void setWalls(boolean[][] walls) {
        this.walls = walls;
    }

    public void setEdges(List<Position> edges) {
        this.edges = edges;
    }

    public void setPacman(Position pacman) {
        this.pacman = pacman;
    }

    public final int getWidth() {
        return walls.length;
    }

    public final int getHeight() {
        return walls[0].length;
    }

    public final boolean isWall(final int x, final int y) {
        return walls[x][y];
    }

    public void turnToStone(List<Position> edges) {
        edges.forEach(this::turnToStone);
    }

    public void turnToStone(Position pos) {
        walls[pos.getX()][pos.getY()] = true;
    }

    public boolean[][] getWalls() {
        return walls;
    }

    public List<Position> getEdges() {
        return edges;
    }

    public Position getPacman() {
        return pacman;
    }

    public Direction getOriginalDirection() {
        return originalDirection;
    }

    public void setOriginalDirection(Direction originalDirection) {
        this.originalDirection = originalDirection;
    }
}
