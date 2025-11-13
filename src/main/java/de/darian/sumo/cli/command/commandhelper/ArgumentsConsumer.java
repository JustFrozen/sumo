package de.darian.sumo.cli.command.commandhelper;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ArgumentsConsumer {

    boolean accept(@NotNull String[] arguments);

}
