package it.jui.semantic;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SemanticCatalog {

    private static final String CATALOG_RESOURCE = "/META-INF/jui/capabilities.json";
    private static final Gson GSON = new Gson();
    private static final SemanticCatalog BUILT_IN = loadBuiltIn();

    private final int schemaVersion;
    private final List<CapabilityDefinition> capabilities;
    private final Map<String, CapabilityDefinition> byId;

    private SemanticCatalog(CatalogDocument document) {
        this.schemaVersion = document.schemaVersion();
        this.capabilities = List.copyOf(document.capabilities());
        Map<String, CapabilityDefinition> index = new LinkedHashMap<>();
        for (CapabilityDefinition capability : capabilities) {
            if (index.put(capability.id(), capability) != null) {
                throw new IllegalArgumentException("Duplicate capability id: " + capability.id());
            }
        }
        this.byId = Map.copyOf(index);
    }

    public static SemanticCatalog builtIn() {
        return BUILT_IN;
    }

    public int schemaVersion() {
        return schemaVersion;
    }

    public List<CapabilityDefinition> capabilities() {
        return capabilities;
    }

    public CapabilityDefinition require(String id) {
        CapabilityDefinition capability = byId.get(id);
        if (capability == null) throw new IllegalArgumentException("Unknown JUI capability: " + id);
        return capability;
    }

    public String compactJson(Set<String> capabilityIds) {
        capabilityIds.forEach(this::require);
        List<CapabilityDefinition> selected = capabilities.stream()
                .filter(capability -> capabilityIds.contains(capability.id()))
                .toList();
        return GSON.toJson(selected);
    }

    public static ApplicationProfile readApplication(InputStream input) throws IOException {
        if (input == null) throw new IOException("Missing application semantic profile");
        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, ApplicationProfile.class);
        }
    }

    private static SemanticCatalog loadBuiltIn() {
        try (InputStream input = SemanticCatalog.class.getResourceAsStream(CATALOG_RESOURCE)) {
            if (input == null) throw new IllegalStateException("Missing " + CATALOG_RESOURCE);
            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                return new SemanticCatalog(GSON.fromJson(reader, CatalogDocument.class));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load the JUI semantic catalog", e);
        }
    }

    private record CatalogDocument(int schemaVersion, List<CapabilityDefinition> capabilities) {
    }
}
