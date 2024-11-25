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

    /**
     * Used for {@link TalkingCitizenParameters.Talking.CHAT} {@link TalkingCitizen}.
     */
    public void chatChat(String s, CommandSender commandSender){
        switch(this.getParameters().type){
            case PERSONAL:
                String name = commandSender.getName();
                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGModel(this.getParameters().modelType,
                            "npc-"+this.getParameters().name+"-"+name, this.getParameters().systemAppend);
                    this.getParameters().models.put(name,newConversationModel);
                }
                this.getParameters().models.get(name).chat(s, commandSender,this.getParameters().range);
                break;
            case SHARED:
                models.get("").chat(s, commandSender, this.getParameters().range);
                break;
        }
    }

}
