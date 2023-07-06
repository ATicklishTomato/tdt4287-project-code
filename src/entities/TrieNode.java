package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrieNode {
    Map<Character, TrieNode> children;
    char c;
    boolean isWord;

    public TrieNode(char c) {
        this.c = c;
        children = new HashMap<>();
    }

    public TrieNode() {
        children = new HashMap<>();
    }

    public void insert(String word) {
        if (word == null || word.isEmpty())
            return;
        char firstChar = word.charAt(0);
        TrieNode child = children.get(firstChar);
        if (child == null) {
            child = new TrieNode(firstChar);
            children.put(firstChar, child);
        }

        if (word.length() > 1)
            child.insert(word.substring(1));
        else
            child.isWord = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrieNode trieNode)) return false;
        return c == trieNode.c && isWord == trieNode.isWord && Objects.equals(children, trieNode.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children, c, isWord);
    }

    @Override
    public String toString() {
        return "TrieNode{" +
                "children=" + children +
                ", c=" + c +
                ", isWord=" + isWord +
                '}';
    }
}
