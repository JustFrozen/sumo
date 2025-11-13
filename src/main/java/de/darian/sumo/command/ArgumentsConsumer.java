package de.darian.sumo.command;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ArgumentsConsumer {

    boolean accept(@NotNull String[] arguments);

}
