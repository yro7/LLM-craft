package fr.yro.llmcraft.Citizens;

import org.bukkit.command.CommandSender;

public class HologramTalkingCitizen extends TalkingCitizen {

    public HologramManager hologramManager;

    public HologramTalkingCitizen(TalkingCitizenParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void chat(String s, CommandSender commandSender) {
        System.out.println("test omfggggggggg!");
    }
}
