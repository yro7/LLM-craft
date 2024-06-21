package fr.yro.llmcraft;

import fr.yro.llmcraft.Helper.Helper;
import fr.yro.llmcraft.Model.IGModelType;
import fr.yro.llmcraft.Model.ListeningModel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e){
        ListeningModel.getModelsListening(e.getPlayer())
                .forEach(model -> model.chat(e.getMessage(), e.getPlayer()));
    }

    @EventHandler
    public void test(AsyncPlayerChatEvent e){

        System.out.println("SETUP NEW HELPER ....");

        IGModelType igModelType = IGModelType.modelsTypes.values().iterator().next();
        Helper helper = new Helper(igModelType,
                "helper-number- one");

        System.out.println("SETUP NEW HELPER : " + helper + " with ig modeltype : " + igModelType);

        helper.chat("HELLO, HOW ARE YOU ? ", e.getPlayer());
    }

}
