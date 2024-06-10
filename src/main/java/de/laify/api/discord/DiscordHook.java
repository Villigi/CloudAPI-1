package de.laify.api.discord;

import de.laify.api.discord.listener.CommandListener;
import de.laify.api.discord.command.DiscordCommandHandler;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Getter
public class DiscordHook {

    private final JDA jda;
    private final String laifyGuildId;
    private final String teamGuildId;
    private final DiscordCommandHandler commandHandler;

    public DiscordHook() {
        this.laifyGuildId = "<SERVER GUILD>";
        this.teamGuildId = "<TEAM GUILD>";

        this.jda = JDABuilder.createDefault("<INSERT YOUR BOT TOKEN>")
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("auf <DeinServer>.de"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new CommandListener(this))
                .build();

        try {
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.commandHandler = new DiscordCommandHandler(this);
    }

    public Guild getLaifyGuild() {
        return getJda().getGuildById(getLaifyGuildId());
    }

    public Guild getTeamGuild() {
        return getJda().getGuildById(getTeamGuildId());
    }

    public MessageChannel getLogChannel() {
        return getTeamGuild().getTextChannelById("<LOG CHANNEL ID>");
    }

}

