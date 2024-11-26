package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;

public class ChatTalkingCitizen extends TalkingCitizen{

    public ChatTalkingCitizen(TalkingCitizenParameters parameters) {
        super(parameters);
    }

    @Override
    public void chat(String s, CommandSender commandSender){
            chatChat(s, commandSender);
    }

    public void chatChat(String s, CommandSender commandSender){
        this.getModel(commandSender).chat(s, commandSender);
    }

}
