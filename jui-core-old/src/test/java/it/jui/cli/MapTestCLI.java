package it.jui.cli;

public class MapTestCLI {

    public static void main(String[] args) throws Exception {

        JuiCLI.main(new String[] {"init", "MapApp.java"});
    
        JuiCLI.main(new String[] {"watch", "MapApp.java"});
    }

}
