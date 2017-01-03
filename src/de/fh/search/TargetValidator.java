package de.fh.search;

import de.fh.util.DIRECTION;
import de.fh.util.Position;

public interface TargetValidator {
    boolean isVisitable(Position info);
    boolean isTarget(Position info, DIRECTION dir);
}
