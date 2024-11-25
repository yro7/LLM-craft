package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;

public class ChatTalkingCitizen extends TalkingCitizen{

    public ChatTalkingCitizen(TalkingCitizenParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void chat(String s, CommandSender commandSender){
            chatChat(s, commandSender);
    }

    public void chatChat(String s, CommandSender commandSender){
        switch(this.getParameters().type){
            case PERSONAL:

                String identifier = this.getIdentifier(commandSender);
                String name = commandSender.getName();

                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGModel(this.getParameters().modelType,
                            identifier, this.getParameters().systemAppend);
                    models.put(name,newConversationModel);
                }
                models.get(name).chat(s, commandSender,this.getParameters().range);
                break;

            case SHARED:
                models.get("").chat(s, commandSender, this.getParameters().range);
                break;
        }
    }

}
