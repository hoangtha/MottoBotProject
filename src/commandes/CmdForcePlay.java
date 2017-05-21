package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdForcePlay implements Commande {

	@Override
	public String getName() {
		return "mottoforceplay";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mottoforcep");
		alias.add("mottofplay");
		alias.add("mottofp");
		alias.add("mfp");
		alias.add("mforcep");
		alias.add("mfplay");
		alias.add("mforceplay");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		
		if(!arguments.equals(""))
		{
			bot.getProperAudioManager().clearQueue(e.getTextChannel(), bot);
		
			boolean rechercheFlag = false;
	//		boolean inVoiceWithMottoBotFlag = false;
			Document doc;
			String url = "https://www.youtube.com/results?q=" + arguments;
			String videoUrl = "";
			
			if(e.getMember().getVoiceState().inVoiceChannel())
			{
				if(!arguments.startsWith(("http")))
				{
					try {
						doc = Jsoup.connect(url).get();
						videoUrl = "https://www.youtube.com" + doc.select("a[class=yt-uix-tile-link yt-ui-ellipsis yt-ui-ellipsis-2 yt-uix-sessionlink      spf-link ]").stream().findAny().map(docs -> docs.attr("href"))
								.orElse("/watch?v=dQw4w9WgXcQ");
						rechercheFlag = true;
					} catch (IOException e1) {
						e1.printStackTrace();
						videoUrl = "";
					}
				}
				else
				{
					videoUrl = arguments;
				}
				bot.getProperAudioManager().loadAndPlay(e.getTextChannel(), videoUrl, bot, e.getMember().getVoiceState().getChannel(), rechercheFlag);
			}
			else
			{
				e.getChannel().sendMessage("<@"+e.getAuthor().getId()+"> :  entre dans un channel vocal pour effectuer cette commande").queue();
			}
		}
	}
}
