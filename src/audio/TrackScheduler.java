package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	
	private ArrayList<String> playlist;
	
	/**
	 * @param player
	 *            The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.playlist = new ArrayList<String>();
	}

	/**
	 * Add the next track to queue or play right away if nothing is in the
	 * queue.
	 *
	 * @param track
	 *            The track to play or add to queue.
	 */
	public void queue(AudioTrack track) {
		// Calling startTrack with the noInterrupt set to true will start the
		// track only if nothing is currently playing. If
		// something is playing, it returns false and does nothing. In that case
		// the player was already playing so this
		// track goes to the queue instead.
		if (!this.player.startTrack(track, true)) {
			this.queue.offer(track);
			this.playlist.add(track.getInfo().title);
		}
	}
	
	public void queuePlayList(AudioPlaylist playlist) {
		// Calling startTrack with the noInterrupt set to true will start the
		// track only if nothing is currently playing. If
		// something is playing, it returns false and does nothing. In that case
		// the player was already playing so this
		// track goes to the queue instead.
		
		List<AudioTrack> list = playlist.getTracks();
		for(int i = 0; i<list.size(); i++)
		{
			if (!this.player.startTrack(list.get(i), true)) {
				this.queue.offer(list.get(i));
				this.playlist.add(list.get(i).getInfo().title);
			}
		}
	}

	/**
	 * Start the next track, stopping the current one if it is playing.
	 */
	public void nextTrack() {
		// Start the next track, regardless of if something is already playing
		// or not. In case queue was empty, we are
		// giving null to startTrack, which is a valid argument and will simply
		// stop the player.

		this.player.startTrack(this.queue.poll(), false);
		if(this.playlist.size()!=0)
		{
			this.playlist.remove(0);
		}
		
	}
	
	public void clearPlaylist()
	{
		this.queue.clear();
		this.playlist.clear();
		this.player.startTrack(this.queue.poll(), false);
	}
	
	public void shufflePlaylist()
	{
		ArrayList<AudioTrack> tempPlaylist;
		tempPlaylist = new ArrayList<AudioTrack>();
		int sizeOfPlaylist = this.playlist.size();
		AudioTrack tmp = null;
		for(int i = 0; i<sizeOfPlaylist; i++)
		{
			try
			{
				tmp = this.queue.take();
				tempPlaylist.add(tmp);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
		Collections.shuffle(tempPlaylist);
		for(int i = 0; i<sizeOfPlaylist; i++)
		{
			this.playlist.set(i, tempPlaylist.get(sizeOfPlaylist-(i+1)).getInfo().title);
			this.queue.add(tempPlaylist.remove(sizeOfPlaylist-(i+1)));
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Only start the next track if the end reason is suitable for it
		// (FINISHED or LOAD_FAILED)
		if (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.STOPPED) {
			nextTrack();
		}
	}

	public List<String> getPlaylist() {
		return this.playlist;
	}
}