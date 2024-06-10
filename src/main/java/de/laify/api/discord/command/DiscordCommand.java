package de.laify.api.discord.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Getter
@AllArgsConstructor
public class DiscordCommand {

    private final CommandDataImpl data;
    private final DiscordCommandStructure structure;

}
