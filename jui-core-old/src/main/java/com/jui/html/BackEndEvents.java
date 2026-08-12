package com.jui.html;

import com.jui.model.JuiNotification;

/**
 * Handles server-side (back-end) events for WebElements.
 */
public class BackEndEvents {

    /**
     * Triggered when the back-end generates an update for the front-end.
     * 
     * @param updateData the data to be sent to the client
     */
	public void onServerUpdate(WebElement element, String action, String command) {
		
    	JuiNotification notification = new JuiNotification(element, action);
		notification.setCommand(command);
		com.jui.JuiNotifier.notifier.onAttributeChanged(notification);
    }

    /**
     * Triggered when the back-end starts a long-running operation.
     * 
     * @param operationId a unique identifier for the operation
     */
    public void onServerOperationStart(String operationId) {
        // Implement logic to notify about operation start
    }

    /**
     * Triggered when a long-running operation on the back-end completes.
     * 
     * @param operationId a unique identifier for the operation
     * @param result the result of the operation
     */
    public void onServerOperationComplete(String operationId, Object result) {
        // Implement logic to notify about operation completion
    }

    /**
     * Triggered when an error occurs on the back-end.
     * 
     * @param errorCode the code identifying the error
     * @param errorMessage a description of the error
     */
    public void onServerError(int errorCode, String errorMessage) {
        // Implement logic to communicate server errors
    }

    /**
     * Triggered when the back-end sends a notification to the client.
     * 
     * @param notificationType the type of the notification
     * @param message the notification message
     */
    public void onServerNotification(String notificationType, String message) {
        // Implement logic to send notifications to the front-end
    }


}
