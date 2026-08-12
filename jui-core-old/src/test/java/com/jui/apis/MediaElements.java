package com.jui.apis;

import static com.jui.JuiApp.jui;

import com.jui.html.elements.media.Video;


public class MediaElements {

	public static void main(String[] args)  {
	
        jui.markdown("""
        		# Media Elements APIs
        		""");

        jui.image("https://raw.githubusercontent.com/mwzero/jui/main/assets/images/components.png", "Jui Components");
        jui.video("https://raw.githubusercontent.com/mwzero/jui/main/assets/images/little-example.webm", Video.Format.Format_16b9, true);
        jui.audio("https://raw.githubusercontent.com/mwzero/jui/main/assets/audio/creepy-halloween-bell-trap-melody-247720.mp3");
        
        jui.server().start();

	}
}
