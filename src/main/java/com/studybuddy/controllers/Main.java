package com.studybuddy.controllers;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;

// Below is my implementation of the Decorator design pattern:


public class Main {
    public static void main(String[] args) {
        var choices = List.of("💎", "📄", "✂️");
        var choicesExtendedWithSampleMethod = new ListDecorator(choices); // Use the design pattern here.
        var rock = choicesExtendedWithSampleMethod.get(0); // 👍  Works
        var player1Choice = choicesExtendedWithSampleMethod.sample(); // 👍  Works
        System.out.println(player1Choice);
    }
}

