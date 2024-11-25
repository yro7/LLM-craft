package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;

public class ChatTalkingCitizen extends TalkingCitizen{

    public ChatTalkingCitizen(TalkingCitizenParameters parameters) {
        this.parameters = parameters;
    }

    public void chat(String s, CommandSender commandSender){
        switch(this.getPrivacy()){

            case PERSONAL:
                String name = commandSender.getName();
                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGModel(this.getModelType(),
                            "npc-"+this.getName()+"-"+name, this.getSystemAppend());
                    models.put(name,newConversationModel);
                }
                models.get(name).chat(s, commandSender,this.getRange());
                break;
            case SHARED:
                models.get("").chat(s, commandSender, this.getRange());
                break;
        }
    }

}
