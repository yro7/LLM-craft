package fr.yronusa.llmcraft.Citizens;

public class Range {

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
