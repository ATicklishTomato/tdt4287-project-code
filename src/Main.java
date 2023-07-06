import entities.Pair;
import entities.Trie;
import entities.TrieNode;

import java.io.*;
import java.util.*;

public class Main {

    static String adapterSequence = "TGGAATTCTCGGGTGCCAAGGAACTCCAGTCACACAGTGATCTCGTATGCCGTCTTCTGCTTG";
    static Map<Integer, Integer> lengthDistribution = new HashMap<>();
    static int totalSequenceErrors = 0;
    static Map<Character, Integer> nucleotideErrorDistribution = new HashMap<>();

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int choice = Integer.MAX_VALUE;
        String menu = """
                m = adapter sequence length
                n = dataset size
                Choose a program:
                1. Task 1 Slow Solution (O(n*m))
                2. Task 1 Fast Solution (Θ(n))
                3. Task 2 Slow Solution (O(n*m^2))
                4. Task 2 Fast Solution (Θ(mn))
                5. Task 4
                6. Task 5
                9. Test Task 1 Slow vs Task 1 Fast
                0. Exit""";
        while (choice > 0) {
            System.out.println(menu);
            choice = Integer.parseInt(s.nextLine());
            Long start = System.currentTimeMillis();
            switch (choice) {
                case 1 -> taskOneAndTwoSlow(0.0);
                case 2 -> taskOneFast();
                case 3 -> {
                    System.out.println("Enter error margin as a fraction between 0 and 1 inclusive with '.' decimal point:");
                    double errorMargin = Double.parseDouble(s.nextLine());
                    start = System.currentTimeMillis();
                    taskOneAndTwoSlow(errorMargin);
                }
                case 4 -> {
                    System.out.println("Enter error margin as a fraction between 0 and 1 inclusive with '.' decimal point:");
                    double errorMargin = Double.parseDouble(s.nextLine());
                    start = System.currentTimeMillis();
                    taskTwoFast(errorMargin);
                }
                case 5 -> fourAgain();
                case 6 -> taskFive();
                case 9 -> compareSlowAndFast();
                case 0 -> System.exit(0);
                default -> System.out.println("Invalid choice");
            }
            Long end = System.currentTimeMillis();
            if (choice > 0) {
                System.out.println("Execution Time: "
                        + (end - start) / 1000
                        + " seconds, and "
                        + (end - start) % 1000
                        + " milliseconds.");
            }
            lengthDistribution = new HashMap<>();
        }
    }

    public static void taskFive() {
        List<Map<Character, Long>> mapList = new ArrayList<>();
        BufferedReader br = null;
        try {
            File file = new File("./src/data/MultiplexedSamples"); // java.io.File
            FileReader fr = new FileReader(file); // java.io.FileReader
            br = new BufferedReader(fr); // java.io.BufferedReader
            String line;
            while ((line = br.readLine()) != null) {
                int counter = 0;
                for (int i = line.length() - 8; i < line.length(); i++) {
                    if (mapList.size() <= counter) {
                        mapList.add(new HashMap<>());
                    }
                    Map<Character, Long> map = mapList.get(counter);
                    char c = line.charAt(i);
                    if (map.containsKey(c)) {
                        map.put(c, map.get(c) + 1);
                    } else {
                        map.put(c, 1L);
                    }
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> barcodes = taskFiveResultBuilder(mapList);
        System.out.println("barcodes: " + barcodes);

        fiveAgain(barcodes);
    }

    public static String taskFourResultBuilder(List<Map<Character, Long>> mapList) {
        StringBuilder result = new StringBuilder();
        for (Map<Character, Long> map : mapList) {
            long max = 0;
            char maxChar = ' ';
            for (Map.Entry<Character, Long> entry : map.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    maxChar = entry.getKey();
                }
            }
            result.append(maxChar);
        }
        return result.toString();
    }

    public static List<String> taskFiveResultBuilder(List<Map<Character, Long>> mapList) {
        List<String> results = new ArrayList<>();
        List<LinkedHashMap<Character,Long>> sortedMapList = new ArrayList<>();
        for (Map<Character, Long> unSortedMap : mapList) {
            LinkedHashMap<Character, Long> sortedMap = new LinkedHashMap<>();
            unSortedMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            sortedMapList.add(sortedMap);
        }
        for (int i = 0; i < sortedMapList.get(0).size(); i++) {
            StringBuilder result = new StringBuilder();
            for (LinkedHashMap<Character, Long> characterLongLinkedHashMap : sortedMapList) {
                result.append(characterLongLinkedHashMap.keySet().toArray()[i]);
            }
            results.add(result.toString());
        }

        List<String> additionalResults = new ArrayList<>();
        for (String result : results) {
            additionalResults.add(result.substring( 4));
            additionalResults.add(result.substring(3));
            additionalResults.add(result.substring(2));
            additionalResults.add(result.substring(1));
        }
        results.addAll(additionalResults);
        results.removeIf(s -> s.contains("N"));
        return results;
    }

    public static void fiveAgain(List<String> barcodes) {
        List<String> withoutBarcode = new ArrayList<>();
        Map<String, List<String>> barcodeHitMap = new HashMap<>();
        for (String barcode : barcodes) {
            barcodeHitMap.put(barcode, new ArrayList<>());
        }
        barcodes.sort(Comparator.comparingInt(String::length));
        Collections.reverse(barcodes);
        BufferedReader br = null;
        int withoutCounter = 0;
        try {
            File file = new File("./src/data/MultiplexedSamples"); // java.io.File
            FileReader fr = new FileReader(file); // java.io.FileReader
            br = new BufferedReader(fr); // java.io.BufferedReader
            String line;
            while ((line = br.readLine()) != null) {
                boolean found = false;
                for (String barcode : barcodes) {
                    if (complexEndsWith(line, barcode)) {
                        line = line.substring(0, line.length() - barcode.length());
                        withoutBarcode.add(line);
                        barcodeHitMap.get(barcode).add(line);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    withoutBarcode.add(line);
                    withoutCounter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String s : withoutBarcode) {
            if (lengthDistribution.containsKey(s.length())) {
                lengthDistribution.put(s.length(), lengthDistribution.get(s.length()) + 1);
            } else {
                lengthDistribution.put(s.length(), 1);
            }
        }

        System.out.println("Number of unique sequences: " + new HashSet<>(withoutBarcode).size());
        System.out.println("Number of sequences without barcode: " + withoutCounter);
        System.out.println("Number of barcodes: " + barcodes.size());
        System.out.println("Dataset size: " + withoutBarcode.size());
        System.out.println("length distribution: ");
        for(Map.Entry<Integer, Integer> entry : lengthDistribution.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("barcode hit distribution: ");
        for(Map.Entry<String, List<String>> entry : barcodeHitMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size());
        }

        for (Map.Entry<String, List<String>> entry : barcodeHitMap.entrySet()) {
            Map<String, Integer> frequencyMap = new HashMap<>();
            for (String s : entry.getValue()) {
                if (frequencyMap.containsKey(s)) {
                    frequencyMap.put(s, frequencyMap.get(s) + 1);
                } else {
                    frequencyMap.put(s, 1);
                }
            }
            writeToCSVStringInt(frequencyMap, "barcode_"+entry.getKey()+"_frequencyMap");
        }
    }

    public static boolean complexEndsWith(String s, String end) {
        if (s.length() < end.length()) {
            return false;
        }
        for (int i = 0; i < end.length(); i++) {
            if (s.charAt(s.length() - end.length() + i) != end.charAt(i) && s.charAt(s.length() - end.length() + i) != 'N') {
                return false;
            }
        }
        return true;
    }

    public static void fourAgain() {
        List<Map<Character, Long>> mapList = new ArrayList<>();
        BufferedReader br = null;
        try {
            File file = new File("./src/data/s_3_sequence_1M.txt"); // java.io.File
            FileReader fr = new FileReader(file); // java.io.FileReader
            br = new BufferedReader(fr); // java.io.BufferedReader
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    if (mapList.size() <= i) {
                        mapList.add(new HashMap<>());
                    }
                    Map<Character, Long> map = mapList.get(i);
                    char c = line.charAt(i);
                    if (map.containsKey(c)) {
                        map.put(c, map.get(c) + 1);
                    } else {
                        map.put(c, 1L);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String result = taskFourResultBuilder(mapList);

        System.out.println(result);
    }

    /**
     * Perform task 1 using a Trie data structure.
     */
    public static void taskOneFast() {
        List<String> reversedItems = new ArrayList<>();
        BufferedReader br = null;
        try {
            File file = new File("./src/data/s_3_sequence_1M.txt"); // java.io.File
            FileReader fr = new FileReader(file); // java.io.FileReader
            br = new BufferedReader(fr); // java.io.BufferedReader
            String line;
            while ((line = br.readLine()) != null) {
                StringBuilder sb = new StringBuilder(line);
                sb.reverse();
                reversedItems.add(sb.toString());
            }
        }
        catch(IOException e) { e.printStackTrace();}
        finally
        {
            try { if (br != null) br.close(); }
            catch(IOException e) { e.printStackTrace(); }
        }

        Trie trie = new Trie(reversedItems);

        Set<String> matches = new HashSet<>();
        for(int i = 1; i < adapterSequence.length(); i++) {
            String prefix = adapterSequence.substring(0, i);
            StringBuilder sb=new StringBuilder(prefix);
            sb.reverse();
            TrieNode node = trie.find(sb.toString());
            if(node != null) {
                matches.addAll(trie.getChildren(node, prefix));
            }
        }
        System.out.println("Count: " + matches.size());
    }

    /**
     * Perform task 2 using a Trie data structure.
     * @param errorMargin   The error margin as a fraction between 0 and 1 inclusive.
     */
    public static void taskTwoFast(double errorMargin) {
        List<String> reversedItems = new ArrayList<>();
        Set<String> uniqueItems = new HashSet<>();

        BufferedReader br = null;
        try {
            File file = new File("./src/data/s_3_sequence_1M.txt"); // java.io.File
            FileReader fr = new FileReader(file); // java.io.FileReader
            br = new BufferedReader(fr); // java.io.BufferedReader
            String line;
            while ((line = br.readLine()) != null) {
                uniqueItems.add(line);
                StringBuilder sb = new StringBuilder(line);
                sb.reverse();
                reversedItems.add(sb.toString());
            }
        }
        catch(IOException e) { e.printStackTrace();}
        finally
        {
            try { if (br != null) br.close(); }
            catch(IOException e) { e.printStackTrace(); }
        }

        Trie trie = new Trie(reversedItems);

        Set<String> matches = new HashSet<>();
        for(int i = 1; i < adapterSequence.length(); i++) {
            String prefix = adapterSequence.substring(0, i);
            StringBuilder sb=new StringBuilder(prefix);
            sb.reverse();
            List<Pair> nodeList = trie.findWithErrorMargin(sb.toString(), prefix, errorMargin);
            if (!nodeList.isEmpty()) {
                for (Pair nodeAndPrefix : nodeList) {
                    matches.addAll(trie.getChildren(nodeAndPrefix.getPairNode(), nodeAndPrefix.getPairPrefix()));
                    System.out.println(nodeAndPrefix.getPairPrefix().length());
                }
            }
        }
        uniqueItems.retainAll(matches);
        System.out.println(uniqueItems.size());
        for(String s : matches) {
            if(s.length() != 50) {
                System.out.println("incorrect length, " + s.length());
            }
        }
        System.out.println("Count: " + matches.size());
    }

    /**
     * Perform task 1 or 2 using a brute force approach.
     * @param errorMargin   The error margin as a fraction between 0 and 1 inclusive.
     */
    public static void taskOneAndTwoSlow(double errorMargin) {
        List<String> matches = new ArrayList<>();
        BufferedReader br = null;
        int total = 0;
        try {
            File file = new File("./src/data/s_3_sequence_1M.txt"); // java.io.File
            FileReader fr = new FileReader(file); // java.io.FileReader
            br = new BufferedReader(fr); // java.io.BufferedReader
            String line;
            while ((line = br.readLine()) != null) {
                if (Main.candidate(line, errorMargin)) {
                    matches.add(line);
                }
                total++;
            }
        }
        catch(IOException e) { e.printStackTrace();}
        finally
        {
            try { if (br != null) br.close(); }
            catch(IOException e) { e.printStackTrace(); }
        }
        System.out.println("Count: " + matches.size());
//        for(Map.Entry<Integer, Integer> entry : lengthDistribution.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
        System.out.println("Average error per sequence: " + (double) totalSequenceErrors / (double) (total));
        System.out.println("Error per nucleotide: ");
        for (Map.Entry<Character, Integer> entry : nucleotideErrorDistribution.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
//        writeToCSV(lengthDistribution, "lengthDistribution" + (int)(errorMargin*100) + "percent");
    }

    /**
     * Figure out of a given has a suffix matching the adapter sequence start.
     * @param s            The string to check.
     * @param errorMargin   The error margin as a fraction between 0 and 1 inclusive.
     * @return            True if the string has a suffix matching the adapter sequence start.
     */
    public static boolean candidate(String s, double errorMargin) {
        for (int i = Math.min(adapterSequence.length(), s.length()); i >= 1; i--) {
            if (i == adapterSequence.length()) {
                if (s.endsWith(adapterSequence)) {
                    return true;
                } else {
                    String str1 = s.substring(s.length() - i);
                    String str2 = adapterSequence.substring(0, i);
                    int mismatch = Main.mismatch(str1, str2);
                    if (mismatch / (double) str2.length() <= errorMargin) {
                        totalSequenceErrors += mismatch;
                        countNucleotideMismatch(str1, str2);
                        if (lengthDistribution.containsKey(s.length() - i)) {
                            int count = lengthDistribution.get(s.length() - i) + 1;
                            lengthDistribution.put(s.length() - i, count);
                        } else {
                            lengthDistribution.put(s.length() - i, 1);
                        }
                        return true;
                    }
                }
            } else {
                if (s.endsWith(adapterSequence.substring(0, i))) {
                    if (lengthDistribution.containsKey(s.length() - i)) {
                        int count = lengthDistribution.get(s.length() - i) + 1;
                        lengthDistribution.put(s.length() - i, count);
                    } else {
                        lengthDistribution.put(s.length() - i, 1);
                    }
                    return true;
                } else {
                    String str1 = s.substring(s.length() - i);
                    String str2 = adapterSequence.substring(0, i);
                    int mismatch = Main.mismatch(str1, str2);
                    if (mismatch / (double) str2.length() <= errorMargin) {
                        totalSequenceErrors += mismatch;
                        countNucleotideMismatch(str1, str2);
                        if (lengthDistribution.containsKey(s.length() - i)) {
                            int count = lengthDistribution.get(s.length() - i) + 1;
                            lengthDistribution.put(s.length() - i, count);
                        } else {
                            lengthDistribution.put(s.length() - i, 1);
                        }
                        return true;
                    }
                }
            }
        }
        if (lengthDistribution.containsKey(s.length())) {
            int count = lengthDistribution.get(s.length()) + 1;
            lengthDistribution.put(s.length(), count);
        }else {
            lengthDistribution.put(s.length(), 1);
        }
        return false;
    }

    /**
     * Calculate the number of mismatches between two strings.
     * @param str1  The first string.
     * @param adapterBit  The second string.
     * @return    The number of mismatches.
     */
    public static int mismatch(String str1, String adapterBit) {
        if (str1.length() != adapterBit.length()) {
            return -1;
        }
        int count = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != adapterBit.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    public static void countNucleotideMismatch(String str1, String adapterBit) {
        if (str1.length() != adapterBit.length()) {
            return;
        }
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != adapterBit.charAt(i)) {
                if (nucleotideErrorDistribution.containsKey(str1.charAt(i))) {
                    int count = nucleotideErrorDistribution.get(str1.charAt(i)) + 1;
                    nucleotideErrorDistribution.put(str1.charAt(i), count);
                } else {
                    nucleotideErrorDistribution.put(str1.charAt(i), 1);
                }
            }
        }
    }

    /**
     * Compare slow and fast implementations of task 1.
     */
    public static void compareSlowAndFast() {
        long totalSlow = 0L;
        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis();
            taskOneAndTwoSlow(0.0);
            Long end = System.currentTimeMillis();
            totalSlow += (end - start);
        }
        long totalFast = 0L;
        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis();
            taskOneFast();
            Long end = System.currentTimeMillis();
            totalFast += (end - start);
        }
        System.out.println("Slow: " + totalSlow / 100 + " ms average");
        System.out.println("Fast: " + totalFast / 100 + " ms average");
        if(totalSlow > totalFast) {
            System.out.println("Fast is " + (totalSlow - totalFast) / 100 + " milliseconds faster.");
        } else {
            System.out.println("Slow is " + (totalFast - totalSlow) / 100 + " milliseconds faster.");
        }
    }

    public static void writeToCSV(Map<Integer, Integer> map, String fileNameWithoutExtension) {
        try {
            FileWriter writer = new FileWriter(fileNameWithoutExtension + ".csv");
            for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
                writer.append(entry.getKey().toString());
                writer.append(',');
                writer.append(entry.getValue().toString());
                writer.append('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToCSVStringInt(Map<String, Integer> map, String fileNameWithoutExtension) {
        try {
            FileWriter writer = new FileWriter(fileNameWithoutExtension + ".csv");
            for(Map.Entry<String, Integer> entry : map.entrySet()) {
                writer.append(entry.getKey());
                writer.append(',');
                writer.append(entry.getValue().toString());
                writer.append('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}