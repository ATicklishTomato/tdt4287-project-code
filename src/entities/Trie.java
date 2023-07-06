package entities;

import java.util.*;

public class Trie {
    private final TrieNode root;

    public Trie(List<String> words) {
        this.root = new TrieNode();
        for (String word : words) {
            this.root.insert(word);
        }
    }

    public TrieNode find(String prefix) {
        TrieNode lastNode = root;
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null)
                return null;
        }
        return lastNode;
    }

    public List<Pair> findWithErrorMargin(String reversedPrefix, String prefix, double errorMargin) {
        int errorCount = (int) (errorMargin*prefix.length());
        List<Pair> nodeList = new ArrayList<>();
        helper_for_errorMargin(root, reversedPrefix, errorCount, nodeList, "");
        return nodeList;
    }

    public void helper_for_errorMargin(TrieNode node, String prefix, int errorCount, List<Pair> nodeList, String newPrefix) {
        if (prefix.isEmpty() && errorCount >= 0) {
            nodeList.add(new Pair(node, newPrefix));
        }

        else if (errorCount >= 0) {
            char firstChar = prefix.charAt(0);
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                char ch = entry.getValue().c;
                String potentialNewPrefix = ch + newPrefix;
                if (ch == firstChar ) {
                    //if (prefix.length() > 1) {
                        helper_for_errorMargin(entry.getValue(), prefix.substring(1), errorCount, nodeList, potentialNewPrefix);
                    //}
                    //else {
                      //  nodeList.add(new Pair(node, potentialNewPrefix));
                    //}
                }
                else {
                    //if (prefix.length() > 1) {
                       helper_for_errorMargin(entry.getValue(), prefix.substring(1), errorCount-1, nodeList, potentialNewPrefix);
                    //}
                    //else {
                    //    nodeList.add(new Pair(node, potentialNewPrefix));
                    //}
                }
            }
        }
    }




    public List<String> getChildren(TrieNode node, String prefix) {
        if (node == null) {
            return new ArrayList<>();
        }
        if (node.children == null || node.children.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add(prefix);
            return result;
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            List<String> children = getChildren(entry.getValue());
            for (String child : children) {
                result.add(child + prefix);
            }
        }
        return result;
    }

    public List<String> getChildren(TrieNode node) {
        if (node == null) {
            return new ArrayList<>();
        }
        if (node.children == null || node.children.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add(node.c + "");
            return result;
        }
        List<String> result = new ArrayList<>();
        char start = node.c;
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            List<String> children = getChildren(entry.getValue());
            for (String child : children) {
                result.add(child + start);
            }
        }
        return result;
    }
}
