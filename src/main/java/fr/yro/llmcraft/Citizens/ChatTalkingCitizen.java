package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;

public class ChatTalkingCitizen extends TalkingCitizen{

    public ChatTalkingCitizen(TalkingCitizenParameters parameters) {
        super(parameters);
        IGModel model = new IGModel(parameters.modelType,"npc-"+parameters.name+"-global", parameters.systemAppend);
        this.models.put("", model);
    }


}
