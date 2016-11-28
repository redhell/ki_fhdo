package de.fh.search;

public interface TargetValidator {
    boolean isVisitable(Position info);
    boolean isTarget(Position info);
}
