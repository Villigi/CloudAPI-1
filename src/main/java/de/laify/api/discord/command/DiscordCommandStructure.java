package de.laify.api.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface DiscordCommandStructure {

    void perform(final SlashCommandInteractionEvent e, final Member member);

}
