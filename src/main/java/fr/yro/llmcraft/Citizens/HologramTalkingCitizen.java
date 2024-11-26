package fr.yro.llmcraft.Citizens;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import eu.decentsoftware.holograms.api.utils.collection.DList;
import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

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
        super(parameters);
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

        this.getModel(commandSender).chat(s,commandSender);
    }

    public Hologram getHologram(CommandSender commandSender){
        String identifier = switch(commandSender){
            case Player p -> p.getName();
            default -> "Console";
        };
        if(!this.holograms.containsKey(identifier)) createHologram(commandSender);
        return this.holograms.get(commandSender.getName());
    }


    /**
     * Creates a {@link Hologram} for a {@link CommandSender}, and puts it in the {@link #holograms} hashmap.
     * @param commandSender
     */
    public void createHologram(CommandSender commandSender){
        switch(this.getParameters().type){
            case PERSONAL:
                String identifier = this.getIdentifier(commandSender);
                String name = commandSender.getName();

                // If the command sender has not yet a IGModel, creates it
                if(!models.containsKey(name)){

                    Hologram newHologram = createBlankHologram(identifier);

                    // The hologram must be private for the command sender
                    newHologram.setDefaultVisibleState(false);
                    if(commandSender instanceof Player p) newHologram.setShowPlayer(p);
                    System.out.println("debug : commandsender name : " + commandSender.getName());

                    IGModel newIGStreamModel = new IGStreamModel(this.getParameters().modelType,
                            identifier, this.getParameters().systemAppend,
                            newHologram);

                    models.put(name,newIGStreamModel);
                    this.holograms.put(commandSender.getName(),newHologram);
                    System.out.print("create full hologram print : ");
                    printHolo(newHologram);
                }
                break;


            case SHARED:
                Hologram globalHologram = new Hologram("npc-"+parameters.name+"-global-hologram", this.getLocation());
                this.holograms.put("", globalHologram);
        }
    }

    /**
     * Creates a blank hologram, with one page that has one empty line.
     * @return
     */
    private Hologram createBlankHologram(String identifier) {
        Hologram newHologram = new Hologram(identifier, this.getLocation());
        DHAPI.addHologramLine(newHologram, "empty content");
        System.out.print("create blank hologram print : ");
        printHolo(newHologram);
        return newHologram;
    }

    public static void printHolo(Hologram holo){
        DList<HologramPage> pages = holo.getPages();
        System.out.print("hologram n° " + holo.getName());
        System.out.print("pages : " + pages);
        HologramPage page0 = pages.getFirst();
        System.out.print("page 0 : " + page0);
        List<HologramLine> lines = page0.getLines();
        System.out.print("lines : " + lines);
        System.out.print("line 0 : " + lines.getFirst());
        System.out.print("line 0 content : " + lines.getFirst().getContent());

    }
}
