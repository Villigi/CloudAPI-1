package de.laify.api.discord.listener;

import de.laify.api.discord.DiscordHook;
import de.laify.api.discord.command.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{

    private final DiscordHook discordBot;

    public CommandListener(final DiscordHook discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        final String commandName = e.getName();

        for(DiscordCommand command : discordBot.getCommandHandler().getCommands()) {
            if(command.getData().getName().equalsIgnoreCase(commandName)) {
                command.getStructure().perform(e, e.getMember());

                break;
            }
        }
    }

}
