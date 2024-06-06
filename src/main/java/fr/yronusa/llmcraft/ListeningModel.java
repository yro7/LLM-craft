package fr.yronusa.llmcraft;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class ListeningModel {

    public static HashMap<IGModel,ListeningModel> listeningModels;

    public IGModel model;
    public List<Player> listenedPlayers;

    public ListeningModel(IGModel model, List<Player> listenedPlayers) {
        this.model = model;

        if(isListening(model)) return;
        this.listenedPlayers = listenedPlayers;
        listeningModels.put(model, this);
    }

    public static void listen(IGModel model, Player p){
        if(isListening(model)) listeningModels.get(model).listenedPlayers.add(p);
        else{
            List<Player> list = new ArrayList<>();
            list.add(p);
            ListeningModel lm = new ListeningModel(model, list);
        }
    }


    public static boolean isListening(IGModel model){
        return listeningModels.keySet().stream().anyMatch(model2 -> model2.equals(model));
    }

    public static List<IGModel> getModelsListening(Player p){
        List<IGModel> res = new ArrayList<>();

        for(ListeningModel lm : listeningModels.values()){
            if(lm.listenedPlayers.contains(p)) res.add(lm.model);
        }

        return res;
    }


}
