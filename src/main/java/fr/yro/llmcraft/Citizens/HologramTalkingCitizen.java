package fr.yro.llmcraft.Citizens;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;

import java.io.Console;
import java.util.HashMap;

public class HologramTalkingCitizen extends TalkingCitizen {

    /**
     * Lazy evaluation of the holograms, so they are not created in the constructor but rather
     * created if needed every time that NPC is used. It's because to be created, we need the Location of the NPC,
     * which can only be retrieved a brief moment of time after the server has fully started.
     *
     *  The string identifies a player by its username, or "Console". If the model is Shared,
     *  the map will only contain one entry with an empty string as key.
     */

    public HashMap<String,Hologram> holograms;
    public HologramTalkingCitizen(TalkingCitizenParameters parameters) {
        this.parameters = parameters;
    }



    @Override
    public void chat(String s, CommandSender commandSender) {
        System.out.println("get name debug : " + this.getName());

       // DHAPI.createHologram(this.getName(), this.getLocation());


        System.out.println("this hologram : " + this.holograms);

      //  this.holograms.set.getPage(0).setLine(0,"bonjour comment cv hihi");
        chatChat(s, commandSender);
    }

    public void chatChat(String s, CommandSender commandSender){

        Hologram hologram = getHologram(commandSender);

    }

    public Hologram getHologram(CommandSender commandSender){
        String identifier = this.getIdentifier(commandSender);
        if(this.holograms.containsKey(commandSender)) createHologram(commandSender);
        return this.holograms.get(commandSender);
    }


    public void createHologram(CommandSender commandSender){
        switch(this.getParameters().type){
            case PERSONAL:
                String identifier = this.getIdentifier(commandSender);
                String name = commandSender.getName();

                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGStreamModel(this.getParameters().modelType,
                            identifier, this.getParameters().systemAppend);
                    TalkingCitizenParameters.models.put(name,newConversationModel);
                }
                break;


            case SHARED:
                Hologram globalHologram = new Hologram("npc-"+parameters.name+"-global-hologram", this.getLocation());
                this.holograms.put("", globalHologram);
        }
    }
}
