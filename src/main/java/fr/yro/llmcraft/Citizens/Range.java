package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModel;

/**
 * Range defines at which distance the Model will speak.
 * By default (if no range is provided to the {@link IGModel}),
 * then the Model will use {@link Range#GLOBAL} as its range.
 */
public class Range {

    public static Range GLOBAL = new Range(Type.GLOBAL,0);

    public enum Type {
        WORLD,
        GLOBAL
    }

    public Type type;
    public int range;

    public Range(Type type, int range) {
        this.type = type;
        this.range = range;
    }
}
