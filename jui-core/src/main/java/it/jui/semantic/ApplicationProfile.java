package it.jui.semantic;

import java.util.List;

public record ApplicationProfile(
        int schemaVersion,
        String id,
        String name,
        String intent,
        String archetype,
        String entrypoint,
        List<String> capabilities,
        List<String> userIntents) {
}
