/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsefile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

/**
 *
 * @author shanelynes
 */
public class ParseFile {

    public static void main(String[] args) {
        int count = 0;
        String directoryName = Paths.get(".").toAbsolutePath().normalize().toString();
        String booksPath = directoryName + "/" + args[0];
        File[] files = returnTxtFiles(booksPath);
        HashMap<HashMap<String, String>, Integer> combos = new HashMap<>();
        ArrayList<String> targetWords = new ArrayList<>();
        try {
            targetWords = readWords("shortwords.txt");
        } catch(Exception e) {
            e.printStackTrace();
        }
        for (File f : files) {
            try {
                ArrayList<String> book = processText(f);
                //ensure we can always look at the previous and next word in for loop conditions
                for (int i = 1; i < book.size() - 2; i++) {
                    if (targetWords.contains(book.get(i))) {
                        String currentWord = book.get(i);
                        String prevWord = book.get(i - 1);
                        String nextWord = book.get(i + 1);
                        HashMap<String, String> prev = new HashMap<>();
                        HashMap<String, String> next = new HashMap<>();
                        prev.put(currentWord, prevWord);
                        next.put(currentWord, nextWord);
                        if (!combos.containsKey(prev)) {
                            combos.put(prev, 1);
                        } else {
                            combos.replace(prev, combos.get(prev) + 1);
                        }
                        
                        if (!combos.containsKey(next)) {
                            combos.put(next, 1);
                        } else {
                            combos.replace(next, combos.get(next) + 1);
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
       int counter = 0;
       for (Map.Entry<HashMap<String, String>, Integer> entry : combos.entrySet()) {
            HashMap<String, String> key = entry.getKey();
            int value = entry.getValue();
            if (value > 50) {
                counter++;
                key.forEach((k, v) -> {
                    System.out.print("Target Word: " + k + " |");
                    System.out.print(" Surrounding Word: " + v + " |");
                });
                System.out.println(" Count: " + value);
            }
        }
        System.out.println("Total 50+ Combinations: " + counter);
    }
    
    public static File[] returnTxtFiles(String dirName) {
        File dir = new File(dirName);
        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String fileName) {
                return fileName.endsWith(".txt");
            }
        });
    }
   
    public static ArrayList<String> processText(File file) throws FileNotFoundException {
        ArrayList<String> words = new ArrayList<>();
        Scanner s = new Scanner(new BufferedReader(new FileReader(file.toString())));
        while(s.hasNext()) {
            String[] wordsInText = s.next().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            for (String str : wordsInText) {
                words.add(str);
            }
        }
        
        return words;
    }
    
    public static ArrayList<String> readWords(String fileName) throws FileNotFoundException {
        ArrayList<String> words = new ArrayList<>();
        Scanner s = new Scanner(new BufferedReader(new FileReader(fileName)));
        while(s.hasNext()) {
            String[] wordsInText = s.next().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            for (String str : wordsInText) {
                words.add(str);
            }
        }
        return words;
    }
    
}
