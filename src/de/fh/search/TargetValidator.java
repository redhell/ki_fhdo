package de.fh.search;

import de.fh.util.DIRECTION;
import de.fh.util.Position;

public interface TargetValidator {

    /**
     * Checks if a position is safe for visit
     */
    boolean isVisitable(Position position);

    /**
     * Checks params for UCS target
     *
     * @param position  Position UCS wants to try as next target
     * @param direction Direction UCS wants to end with
     * @return returns true if the target is valid
     */
    boolean isTarget(Position position, DIRECTION direction);
}
