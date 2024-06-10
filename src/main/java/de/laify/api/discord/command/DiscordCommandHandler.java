package de.laify.api.discord.command;

import de.laify.api.discord.DiscordHook;
import lombok.Getter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommandHandler {

    @Getter
    private final List<DiscordCommand> commands;
    private final CommandListUpdateAction teamCommandAction, laifyServerCommandAction;

    public DiscordCommandHandler(final DiscordHook discord) {
        this.commands = new ArrayList<>();
        this.teamCommandAction = discord.getTeamGuild().updateCommands();
        this.laifyServerCommandAction = discord.getLaifyGuild().updateCommands();
    }

    public DiscordCommandHandler addCommand(final DiscordCommand command, final boolean publicCommand) {
        commands.add(command);

        if(publicCommand) {
            laifyServerCommandAction.addCommands(command.getData());
        } else {
            teamCommandAction.addCommands(command.getData());
        }

        return this;
    }

    public DiscordCommandHandler update() {
        teamCommandAction.queue();
        laifyServerCommandAction.queue();

        return this;
    }

}
