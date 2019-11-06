package com.studybuddy.controllers;

import java.util.ArrayList;
import java.util.List;

public class DuckTyping {

    public static class Rock {
        public String toString() { return "💎"; }

        public boolean beats(Object otherPlayerChoice) {
            return otherPlayerChoice instanceof Scissors;
        }
    }

    public static class Paper {
        public String toString() { return "📄"; }

        public boolean beats(Object otherPlayerChoice) {
            return otherPlayerChoice instanceof Rock;
        }
    }

    public static class Scissors {
        public String toString() { return "✂️"; }

        public boolean beats(Object otherPlayerChoice) {
            return otherPlayerChoice instanceof Paper;
        }
    }

    public static Object randomChoice() {
        double randomNumber = Math.random();
        if (randomNumber < 1 / 3) return new Rock();
        if (randomNumber < 2 / 3) return new Paper();
        else { return new Scissors(); }
    }

    public static void main(String[] args) {
        Object player1Choice = randomChoice();
        Object player2Choice = randomChoice();

        System.out.println("Player 1 chose " + player1Choice.toString());
        System.out.println("Player 2 chose " + player2Choice.toString());
        if (player1Choice instanceof Rock) {
            if (((Rock) player1Choice).beats(player2Choice)) System.out.println("Player 1 wins!");
            else if (((Rock) player2Choice).beats(player1Choice)) System.out.println("Player 2 wins!");
            else System.out.println("It’s a draw!");
        } else if (player1Choice instanceof Paper) {
            if (((Paper) player1Choice).beats(player2Choice)) System.out.println("Player 1 wins!");
            else if (((Paper) player2Choice).beats(player1Choice)) System.out.println("Player 2 wins!");
            else System.out.println("It’s a draw!");
        } else if (player1Choice instanceof Scissors) {
            if (((Scissors) player1Choice).beats(player2Choice)) System.out.println("Player 1 wins!");
            else if (((Scissors) player2Choice).beats(player1Choice)) System.out.println("Player 2 wins!");
            else System.out.println("It’s a draw!");
        }
        else { System.out.println("It’s a draw!"); }
    }
}


