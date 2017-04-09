package audio;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import main.Main;
import manager.GuildMusicManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class AudioManagerMotto {
	
	List<String> playlist;

	public AudioManagerMotto() {
		this.playlist = new ArrayList<String>();
	}

	public void loadAndPlay(final TextChannel channel, final String trackUrl, Main bot) {

		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild(), bot);

		AudioPlayerManager playerManager = bot.getPlayerManager();
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				channel.sendMessage("Ajouter à la playlist " + track.getInfo().title).queue();
				play(channel.getGuild(), musicManager, track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();

				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}

				channel.sendMessage("Ajouter à la playlist " + firstTrack.getInfo().title + " (premier titre de la musique "
						+ playlist.getName() + ")").queue();

				play(channel.getGuild(), musicManager, firstTrack);
			}

			@Override
			public void noMatches() {
				channel.sendMessage("Rien trouvé avec " + trackUrl).queue();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Je ne peut pas jouer : " + exception.getMessage()).queue();
			}
		});
	}
	
	private synchronized static GuildMusicManager getGuildAudioPlayer(Guild guild, Main bot) {
		long guildId = Long.parseLong(guild.getId());
		GuildMusicManager musicManager = bot.getMusicManagers().get(guildId);

		if (musicManager == null) {
			musicManager = new GuildMusicManager(bot.getPlayerManager());
			bot.getMusicManagers().put(guildId, musicManager);
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		
		return musicManager;
	}

	public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
		connectToFirstVoiceChannel(guild.getAudioManager());

		musicManager.scheduler.queue(track);
		this.playlist.add(track.getInfo().title);
	}

	public void skipTrack(TextChannel channel, Main bot) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild(), bot);
		musicManager.scheduler.nextTrack();
		channel.sendMessage("Passer à la prochaine musique.").queue();
	}

	public static void connectToFirstVoiceChannel(AudioManager audioManager) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
				audioManager.openAudioConnection(voiceChannel);
				break;
			}
		}
	}
	
	public void clearQueue(TextChannel channel, Main bot) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild(), bot);
		this.playlist.clear();
		for(int i = 0; i<100; i++)
			musicManager.scheduler.nextTrack();
	}

	

}
