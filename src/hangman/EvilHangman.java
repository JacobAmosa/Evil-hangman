package hangman;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args){
        String dictionary = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);
        Scanner scan = new Scanner(System.in);
        Set<String> wordList = new HashSet<String>();

        EvilHangmanGame hm = new EvilHangmanGame();
        File myFile = new File(dictionary);
        try {
            hm.startGame(myFile, wordLength);
        }catch (IOException e) {
            e.printStackTrace();
        }catch (EmptyDictionaryException e) {
            e.printStackTrace();
        }
        String currentGuess = hm.getKey();
        while(guesses > 0){
            System.out.println("You have " + guesses + " guesses left");
            System.out.println("Used letters: " + hm.getUsedLetters());
            System.out.println("Word: " + currentGuess);
            System.out.print("Enter guess: ");
            char guess = scan.next().charAt(0);
            while(!Character.isAlphabetic(guess)){
                System.out.println("Invalid input");
                System.out.print("Enter guess: ");
                guess = scan.next().charAt(0);
            }
            try {
                wordList = hm.makeGuess(guess);
            } catch (GuessAlreadyMadeException e) {
                System.out.println("You already used that letter\n");
                continue;
            }
            if(hm.getKey().equals(currentGuess)){
                System.out.println("Sorry there are no "+guess+"'s\n");
                guesses--;
            } else {
                int count =0;
                currentGuess = hm.getKey();
                for(char c:currentGuess.toCharArray()){
                    if(c == guess){
                        count++;
                    }
                }
                String response = count > 1 ? "are "+count+" "+guess+"'s":"is 1 " + guess;
                if(!currentGuess.contains("-")){
                    System.out.println("You Win!\nThe word was: "+currentGuess);
                    scan.close();
                    return;
                }
                System.out.println("Yes there " + response+ "\n");
            }
        }
        for(String word:wordList){
            currentGuess = word;
            break;
        }
        System.out.println("You lose!\nThe word was: "+currentGuess);

        scan.close();
    }
}
