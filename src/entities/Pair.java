package entities;

public class Pair {
    TrieNode node;
    String prefix;

    Pair(TrieNode node, String prefix) {
        this.node = node;
        this.prefix = prefix;
    }

    public TrieNode getPairNode() {
        return node;
    }

    public String getPairPrefix() {
        return prefix;
    }
}
