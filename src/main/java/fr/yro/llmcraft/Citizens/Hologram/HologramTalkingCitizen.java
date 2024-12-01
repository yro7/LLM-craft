package fr.yro.llmcraft.Citizens.Hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import eu.decentsoftware.holograms.api.utils.collection.DList;
import fr.yro.llmcraft.Citizens.TalkingCitizen;
import fr.yro.llmcraft.Citizens.TalkingCitizenParameters;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelParameters;
import org.bukkit.Location;
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
        if(!this.holograms.containsKey("")) createGlobalHologram();
        chatChat(s, commandSender);
    }

    private void createGlobalHologram() {
        Hologram globalHologram = createBlankHologram("npc-"+this.getParameters().name+"-global-hologram");
        this.holograms.put("", globalHologram);
        TalkingCitizenParameters params = this.getParameters();
        IGModel globalModel = new IGStreamModel(params.modelType,"npc-"+params.name+"-global",
                params.systemAppend,globalHologram, this.extractColor(), this.getBaseY());
        this.models.put("", globalModel);
    }

    public void chatChat(String s, CommandSender commandSender){
        this.getModel(commandSender).chat(s,commandSender);
    }

    public Hologram getHologram(CommandSender commandSender){
        String identifier = switch(commandSender){
            case Player p -> p.getName();
            default -> "Console";
        };
        // If it's a shared model, return the global hologram :
        if(this.getVisibility().equals(IGModelParameters.Visibility.SHARED)) return this.holograms.get("");

        if(!this.holograms.containsKey(identifier)) createHologram(commandSender);


        // If it's a private, return the hologram identified by commandSender's name
        return this.holograms.get(commandSender.getName());
    }


    /**
     * Creates a {@link Hologram} for a {@link CommandSender}, and puts it in the {@link #holograms} hashmap.
     * @param commandSender
     */
    public void createHologram(CommandSender commandSender){
        switch(this.getVisibility()){
            case PERSONAL:
                String identifier = this.getIdentifier(commandSender);
                String name = commandSender.getName();

                // If the command sender has not yet a IGModel, creates it
                if(!models.containsKey(name)){

                    Hologram newHologram = createBlankHologram(identifier);

                    // The hologram must be private for the command sender
                    newHologram.setDefaultVisibleState(false);
                    if(commandSender instanceof Player p) newHologram.setShowPlayer(p);
                    IGModel newIGStreamModel = new IGStreamModel(this.getParameters().modelType,
                            identifier, this.getParameters().systemAppend,
                            newHologram, this.extractColor(),this.getBaseY());

                    models.put(name,newIGStreamModel);
                    this.holograms.put(commandSender.getName(),newHologram);
                }
                break;


            case SHARED:
                Hologram globalHologram = createBlankHologram("npc-"+this.getParameters().name+"-global-hologram");
                this.holograms.put("", globalHologram);
        }
    }

    /**
     * Creates a blank hologram, with one page that has one empty line.
     * @return
     */
    private Hologram createBlankHologram(String identifier) {
        Location loc = this.getLocation();
        loc.setY(getBaseY());
        System.out.print("creating new hologram with name : " + identifier);
        Hologram newHologram = new Hologram(identifier, loc);
        DHAPI.addHologramLine(newHologram, "");
        return newHologram;
    }

    public static void printHolo(Hologram holo){
        try{
            DList<HologramPage> pages = holo.getPages();
            System.out.print("hologram n° " + holo.getName());
            System.out.print("pages : " + pages);
            HologramPage page0 = pages.getFirst();
            System.out.print("page 0 : " + page0);
            List<HologramLine> lines = page0.getLines();
            System.out.print("lines : " + lines);
            System.out.print("line 0 : " + lines.getFirst());
            System.out.print("line 0 content : " + lines.getFirst().getContent());
        } catch(Exception e){
            System.out.println("shady exception idk");
        }

    }

    /** TODO : Tries to retrieve a color to paint the hologram with.
     * The color is retrieved from the name of the NPC: example, if the NPC is named
     * "§e§lBlacksmith" the return value will be "§e"
     * @return A Minecraft color-code formatted string (e.g. §e for yellow, §4 for red...)
     *
     */
    public String extractColor(){
       return "§e";
    }

    public double getBaseY(){
        return this.getLocation().getY()+3.5;
    }
}
