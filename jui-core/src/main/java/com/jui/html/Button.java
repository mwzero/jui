package com.jui.html;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Button extends WebComponent {
	
    private final String label;
    private final String type;
    private final String onClick;

    public Button(String label, String type, String onClick, Runnable onServerSide) {
        this.label = label;
        this.onClick = onClick;
        this.type = type;
        this.onServerSide = onServerSide;
    }

	@Override
	public String render() {
		
		log.debug("Rendering [{}] [{}]", this.getId(), this.getKey());
		
		String js = onClick;
		
		if ( this.onServerSide != null ) {
			js= "%s;sendClick('%s');return false;".formatted(this.onClick, this.getKey());
		}
		
		return "<button onclick=\"" + js + "\" class=\"btn btn-" + type + "\">" + label + "</button>";
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
