package com.studybuddy.controllers;

import java.util.ArrayList;
import java.util.List;

public class DuckTyping {

    public static interface Choice {
        String toString();
        boolean beats(Choice otherPlayerChoice);
    }

    public static class Rock implements Choice {
        public String toString() { return "💎"; }

        public boolean beats(Choice otherPlayerChoice) {
            return otherPlayerChoice instanceof Scissors;
        }
    }

    public static class Paper implements Choice {
        public String toString() { return "📄"; }

        public boolean beats(Choice otherPlayerChoice) {
            return otherPlayerChoice instanceof Rock;
        }
    }

    public static class Scissors implements Choice{
        public String toString() { return "✂️"; }

        public boolean beats(Choice otherPlayerChoice) {
            return otherPlayerChoice instanceof Paper;
        }
    }

    public static Choice randomChoice() {
        double randomNumber = Math.random();
        if (randomNumber < 1.0 / 3) return new Rock();
        else if (randomNumber < 2.0 / 3) return new Paper();
        else { return new Scissors(); }
    }

    public static void main(String[] args) {
        Choice player1Choice = randomChoice();
        Choice player2Choice = randomChoice();

        System.out.println("Player 1 chose " + player1Choice.toString());
        System.out.println("Player 2 chose " + player2Choice.toString());
        if (player1Choice.beats(player2Choice)) System.out.println("Player 1 wins!");
        else if (player2Choice.beats(player1Choice)) System.out.println("Player 2 wins!");
        else System.out.println("It’s a draw!");
    }
}


