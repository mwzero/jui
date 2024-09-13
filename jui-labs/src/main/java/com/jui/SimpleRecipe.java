package com.jui;

public class SimpleRecipe {

	public static void main(String[] args)  {

        App.button("Cliccami", () -> {
            System.out.println("Bottone cliccato!");
        });
        
		App.start();

	}
}
