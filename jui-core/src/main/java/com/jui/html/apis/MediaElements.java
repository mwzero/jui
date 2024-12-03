package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.media.Audio;
import com.jui.html.elements.media.Image;
import com.jui.html.elements.media.Video;

public class MediaElements extends BaseElements {
	
	public MediaElements(WebElementContext context) {
		
		super(context);
		
	}
	
	public Image image(String href, String caption) {

		Image image = new Image(href, caption);
		context.add(image);

		return image;
		
	}
	
	public Video video(String href, Video.Format format, Boolean allowFullScreen) {
		Video video = new Video(href, format, allowFullScreen);
		context.add(video);
		return video;
	}
	
	public Audio audio(String href) {
		Audio audio = new Audio(href);
		context.add(audio);
		return audio;
	}
}
