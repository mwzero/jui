package it.jui.semantic;

import java.util.List;

public record CapabilityDefinition(
        String id,
        String intent,
        String interaction,
        List<String> methods,
        List<String> userIntents,
        List<String> related) {
}
