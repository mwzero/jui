package com.jui.html;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.extern.java.Log;

/**
 * Handles client-side (front-end) events for WebElements.
 */
@Log
public class FrontEndEvents {

	//onUpdateAction to run 
	BiConsumer<String, Map<String, Object>> onUpdateActionBi;
	Consumer<Map<String, Object>> onUpdateActionSingle;
	Runnable onUpdateAction;
	
    /**
     * Triggered when the element is loaded into the DOM.
     */
    public void onLoad() {
        // Implement logic for element load
    }
    
    /**
     * Triggered when the element is updated asynchronously.
     */
    public void onClick(BiConsumer<String, Map<String, Object>> runnable) {onUpdateActionBi=runnable;}
    public void onClick(Consumer<Map<String, Object>> runnable) {onUpdateActionSingle=runnable;}
    public void onClick(Runnable runnable) {onUpdateAction=runnable;}
    
    public void onUpdate(String action, Map<String, Object> payload) {
    	
    	log.fine("Performing action %s".formatted(action));
    	
    	if ( onUpdateActionBi != null ) 
    		onUpdateActionBi.accept(action, payload);
        else if ( onUpdateActionSingle != null ) 
    		onUpdateActionSingle.accept(payload);
    	else 
    		onUpdateAction.run();
        
    }
    public boolean isOnUpdateDefined() {
    	if ( ( onUpdateActionBi != null ) || ( onUpdateActionSingle != null ) || (onUpdateAction!= null)) return true;
    	return false;
    }

    /**
     * Triggered when an asynchronous operation is completed.
     */
    public void onComplete() {
        // Implement logic for task completion
    }

    /**
     * Triggered when an error occurs during a front-end operation.
     * 
     * @param t the Throwable representing the error
     */
    public void onError(Throwable t) {
        // Implement error handling logic
    }

    /**
     * Triggered before the element is rendered.
     */
    public void onBeforeRender() {
        // Implement logic before rendering
    }

    /**
     * Triggered after the element is rendered.
     */
    public void onAfterRender() {
        // Implement logic after rendering
    }

    /**
     * Handles generic actions triggered asynchronously by the front-end.
     * 
     * @param action the action name
     * @param payload the associated data payload
     */
    public void onAction(String action, Object payload) {
        // Implement specific action handling
    }

    /**
     * Triggered when the element is removed from the DOM.
     */
    public void onDetach() {
        // Implement cleanup logic
    }
}

