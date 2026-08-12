package com.jui.html.elements;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an item in an unordered HTML list (&lt;ul&gt;).
 * This class provides attributes to define the label, icon, 
 * external link, and content associated with the list item.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Ensures {@code label} is required and cannot be {@code null}.</li>
 *   <li>Uses Lombok annotations to reduce boilerplate code.</li>
 *   <li>Allows defining an icon, an external link, and additional content.</li>
 * </ul>
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     UnorderedListItem item = new UnorderedListItem("Home");
 * 	   item.setIcon("home-icon");
 * 	   item.setLink("https://example.com");
 * 	   item.setContent("Welcome to the home page!");
 * 	   item.setContainerId("1233");
 * </pre>
 *
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UnorderedListItem {

    /**
     * The text displayed for the list item. This field is required and cannot be {@code null}.
     */
    @NonNull
    private String label;

    /**
     * The icon associated with the list item (optional).
     * This can be an icon class (e.g., FontAwesome) or an image URL.
     */
    private String icon;

    /**
     * An external URL that opens when the list item is selected (optional).
     */
    private String link;

    /**
     * The content displayed when the item is selected (optional).
     */
    private String content;

	/**
     * The content displayed when the item is selected (optional). referring to a Jui Container Id.
     */
    private String containerId;

}