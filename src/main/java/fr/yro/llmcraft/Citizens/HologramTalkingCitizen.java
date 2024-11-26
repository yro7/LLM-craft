package fr.yro.llmcraft.Citizens;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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
        System.out.println("initializeed new HTC.");
        this.parameters = parameters;
        this.holograms = new HashMap<>();
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
        System.out.println("Retrieved hologram for player " + commandSender + " : " + hologram.getName());
        System.out.println("holograms : " + holograms.keySet());

        /**
         * chat with hologram logic :
         * - Launch the IGStreamModel
         *   at each next token generated, update the hologram
         */

        /**
         * update hologram logic :
         *
         * - Compute the needed location of the hologram and move if necessary
         * - compute the formatting
         * - change formatting
         */


        this.models.

    }

    public Hologram getHologram(CommandSender commandSender){
        String identifier = switch(commandSender){
            case Player p -> p.getName();
            default -> "Console";
        };
        if(!this.holograms.containsKey(identifier)) createHologram(commandSender);
        return this.holograms.get(commandSender.getName());
    }


    public void createHologram(CommandSender commandSender){
        switch(this.getParameters().type){
            case PERSONAL:
                String identifier = this.getIdentifier(commandSender);
                String name = commandSender.getName();

                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGStreamModel(this.getParameters().modelType,
                            identifier, this.getParameters().systemAppend);
                    models.put(name,newConversationModel);

                    Hologram hologram = new Hologram(identifier, this.getLocation());
                    hologram.setDefaultVisibleState(false);
                    if(commandSender instanceof Player p) hologram.setShowPlayer(p);
                    System.out.println("debug : commandsender name : " + commandSender.getName());
                    this.holograms.put(commandSender.getName(),hologram);
                }
                break;


            case SHARED:
                Hologram globalHologram = new Hologram("npc-"+parameters.name+"-global-hologram", this.getLocation());
                this.holograms.put("", globalHologram);
        }
    }
}
