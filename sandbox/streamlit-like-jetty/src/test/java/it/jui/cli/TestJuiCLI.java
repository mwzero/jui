package it.jui.cli;

public class TestJuiCLI {

    public static void main(String[] args) throws Exception {

        JuiCLI.main(new String[] {"init", "TestApp.java"});
    
        JuiCLI.main(new String[] {"watch", "TestApp.java"});
    }

}