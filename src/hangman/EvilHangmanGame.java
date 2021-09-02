package hangman;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.lang.StringBuilder;

public class EvilHangmanGame implements IEvilHangmanGame {

    private Set<String> words;
    private int length;
    private String key;
    Set<Character> usedLetters = new TreeSet<Character>();
    private Set<String> fileContent = new TreeSet<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsedLetters(){
        StringBuilder result = new StringBuilder();
        for(char c:usedLetters){
            result.append(c).append(" ");
        }
        return result == null ? "" :result.toString();
    }

    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException  {
        words =  addWords(dictionary, wordLength);
        length = wordLength;
        StringBuilder newKey = new StringBuilder();
        int i = 0;
        while(i < length){
            newKey.append('-');
            i++;
        }
        key = newKey.toString();


        Scanner scanner = new Scanner(dictionary);
        while(scanner.hasNext()){
            String line = scanner.next().toLowerCase();
            if (line.length() == wordLength){
                fileContent.add(line);
            }
        }
        scanner.close();
        if (fileContent.size() == 0){
            throw new EmptyDictionaryException("Empty dictionary");
        }


    }

    private Set<String> addWords(File dictionary, int wordLength){
        Set<String> result = new HashSet<String>();
        try {
            Scanner in = new Scanner(dictionary);
            while(in.hasNext()){
                String word = in.next();
                if (word.length() == wordLength){
                    result.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);
        if(usedLetters.contains(guess)){
            throw new GuessAlreadyMadeException();
        }
        else {
            usedLetters.add(guess);
        }
        Map<String,Set<String>> group = new HashMap<String, Set<String>>();
        for(String word:words){
            String key = makeKey(word,guess);
            if(!group.containsKey(key)){
                group.put(key,new HashSet<String>());
            }
            group.get(key).add(word);
        }
        // limit by size
        group = getLargestSets(group);
        return findPrioritySet(group, guess);
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return null;
    }

    private Set<String> findPrioritySet(Map<String,Set<String>> input,char guess){
        int minScore = Integer.MAX_VALUE;
        for(String temKey:input.keySet()){
            int weight = 1;
            int count = 0;
            int weightedScore = 0;
            for(int i = temKey.length()-1;i>=0;i--){
                if(temKey.charAt(i) == guess){
                    weightedScore+=++count*weight;
                }
                weight*=2;
            }
            if(weightedScore < minScore){
                key = temKey;
                minScore = weightedScore;
            }
        }
        words = input.get(key);
        return words;
    }

    private Map<String,Set<String>> getLargestSets(Map<String,Set<String>> subsets){
        int largest = 0;
        for(Set<String> set:subsets.values()){
            largest = set.size() > largest ? set.size() : largest;
        }

        Map<String,Set<String>> prunedMap = new HashMap<String, Set<String>>();
        for(String key:subsets.keySet()){
            Set<String> set = subsets.get(key);
            if(set.size() == largest){
                prunedMap.put(key,set);
            }
        }
        return prunedMap;
    }

    private String makeKey(String word, char guess){
        StringBuilder newKey = new StringBuilder();
        for(int i = 0; i < word.length();i++){
            char c = word.charAt(i);
            if(c == guess){
                newKey.append(c);
            } else {
                newKey.append(key.charAt(i));
            }
        }
        return newKey.toString();
    }
}
