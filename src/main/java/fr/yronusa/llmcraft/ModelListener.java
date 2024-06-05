package fr.yronusa.llmcraft;

import org.bukkit.entity.Player;

import java.util.List;

public class ModelListener {

    public static List<ModelListener> modelListeners;

    Player player;
    int numberOfListen;
    Model.MODEL modelType;


    public ModelListener(Player p,Model.MODEL modelType, int numberOfListen) {
        this.player = p;
        this.numberOfListen = numberOfListen;
        this.modelType = modelType;
    }

    public static ModelListener getListener(Player p){
        for(ModelListener ml : modelListeners){
            if(ml.player.equals(p)) return ml;
        }
        return null;
    }


}
