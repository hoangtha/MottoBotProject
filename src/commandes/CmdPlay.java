package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPlay implements Commande {

	@Override
	public String getName() {
		return "mottoplay";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mottop");
		alias.add("mp");
		alias.add("mplay");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		if(!arguments.equals(""))
		{
			e.getChannel().sendTyping().queue();
			boolean rechercheFlag = false;
//			boolean inVoiceWithMottoBotFlag = false;
			Document doc;
			String url = "https://www.youtube.com/results?q=" + arguments;
			String videoUrl = "";
			String videoUrlSuffix = "";
			if(e.getMember().getVoiceState().inVoiceChannel())
			{
	//			List<Member> list = e.getMember().getVoiceState().getChannel().getMembers();
	//			for(int i = 0; i<list.size(); i++)
	//			{
	//				if (list.get(i).getEffectiveName().equals("Motto Bot"))
	//				{
	//					inVoiceWithMottoBotFlag = true;
	//					break;
	//				}
	//			}
	//			
	//			if(inVoiceWithMottoBotFlag)
	//			{
					if(!arguments.startsWith(("http")))
					{
						try {
							doc = Jsoup.connect(url).get();
							videoUrl = "https://www.youtube.com";
							videoUrlSuffix = doc.select("a[class=yt-uix-tile-link yt-ui-ellipsis yt-ui-ellipsis-2 yt-uix-sessionlink      spf-link ]").stream().findAny().map(docs -> docs.attr("href"))
									.orElse("");
							if (videoUrlSuffix.equals(""))
							{
								videoUrl = "";
							}
							else
							{
								videoUrl += videoUrlSuffix;
							}
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
	//			}
	//			else
	//			{
	//				e.getChannel().sendMessage("pin pon ! entre dans le meme channel que le bot stp").queue();
	//			}
			}
			else
			{
				e.getChannel().sendMessage("<@"+e.getAuthor().getId()+"> : entre dans un channel vocal pour effectuer cette commande").queue();
			}
		}
		
	}
}
