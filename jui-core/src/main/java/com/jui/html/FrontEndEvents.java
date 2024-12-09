package com.jui.html;

/**
 * Handles client-side (front-end) events for WebElements.
 */
public class FrontEndEvents {

    /**
     * Triggered when the element is loaded into the DOM.
     */
    public void onLoad() {
        // Implement logic for element load
    }

    /**
     * Triggered when the element is updated asynchronously.
     */
    public void onUpdate() {
        // Implement logic for element update
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

