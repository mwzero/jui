package it.jui.semantic;

import it.jui.apis.AuthElements;
import it.jui.apis.ChartElements;
import it.jui.apis.CrudElements;
import it.jui.apis.DataElements;
import it.jui.apis.FormElements;
import it.jui.apis.InputElements;
import it.jui.apis.LayoutElements;
import it.jui.apis.ListElements;
import it.jui.apis.MapElements;
import it.jui.apis.MediaElements;
import it.jui.apis.NavigationElements;
import it.jui.apis.StatusElements;
import it.jui.apis.TextElements;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemanticCatalogTest {

    private static final List<Class<?>> API_TYPES = List.of(
            AuthElements.class, ChartElements.class, CrudElements.class, DataElements.class,
            FormElements.class, InputElements.class, LayoutElements.class, ListElements.class,
            MapElements.class, MediaElements.class, NavigationElements.class,
            StatusElements.class, TextElements.class);

    @Test
    void everyPublicGenerationApiHasSemanticMeaning() {
        Set<String> publicMethods = new HashSet<>();
        for (Class<?> type : API_TYPES) {
            for (Method method : type.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers()) && !method.isAnnotationPresent(Deprecated.class)) {
                    publicMethods.add(method.getName());
                }
            }
        }

        Set<String> documentedMethods = new HashSet<>();
        for (CapabilityDefinition capability : SemanticCatalog.builtIn().capabilities()) {
            documentedMethods.addAll(capability.methods());
        }

        assertEquals(publicMethods, documentedMethods,
                "The semantic catalog must cover exactly the public generation-facing API");
    }

    @Test
    void capabilityDefinitionsAreCompleteAndConsistent() {
        SemanticCatalog catalog = SemanticCatalog.builtIn();
        assertEquals(1, catalog.schemaVersion());
        Set<String> interactions = Set.of("display", "stateful", "event", "layout");

        Set<String> ids = new HashSet<>();
        for (CapabilityDefinition capability : catalog.capabilities()) {
            assertTrue(ids.add(capability.id()), "Duplicate capability: " + capability.id());
            assertTrue(capability.id().matches("[a-z]+(?:[.-][a-z]+)*"), capability.id());
            assertFalse(capability.intent().isBlank(), capability.id());
            assertTrue(interactions.contains(capability.interaction()), capability.id());
            assertFalse(capability.methods().isEmpty(), capability.id());
            assertFalse(capability.userIntents().isEmpty(), capability.id());
        }
        for (CapabilityDefinition capability : catalog.capabilities()) {
            capability.related().forEach(catalog::require);
        }
    }

    @Test
    void everyExampleApplicationHasAValidSemanticProfile() throws Exception {
        Path repository = findRepositoryRoot();
        List<Path> profiles;
        try (var paths = Files.walk(repository.resolve("apps"))) {
            profiles = paths.filter(path -> path.endsWith("META-INF/jui/application.json")).sorted().toList();
        }

        assertEquals(6, profiles.size(), "Every application must provide a semantic profile");
        Set<String> applicationIds = new HashSet<>();
        for (Path profilePath : profiles) {
            try (InputStream input = Files.newInputStream(profilePath)) {
                ApplicationProfile profile = SemanticCatalog.readApplication(input);
                assertEquals(1, profile.schemaVersion(), profilePath.toString());
                assertTrue(applicationIds.add(profile.id()), "Duplicate application id: " + profile.id());
                assertFalse(profile.intent().isBlank(), profile.id());
                assertFalse(profile.archetype().isBlank(), profile.id());
                assertFalse(profile.entrypoint().isBlank(), profile.id());
                assertFalse(profile.capabilities().isEmpty(), profile.id());
                assertFalse(profile.userIntents().isEmpty(), profile.id());
                profile.capabilities().forEach(SemanticCatalog.builtIn()::require);
            }
        }
    }

    @Test
    void producesACompactSubsetForAnLlmPrompt() {
        String json = SemanticCatalog.builtIn().compactJson(Set.of("data.crud", "form.typed"));

        assertTrue(json.contains("data.crud"));
        assertTrue(json.contains("form.typed"));
        assertFalse(json.contains("map.interactive"));
    }

    private Path findRepositoryRoot() {
        Path candidate = Path.of("").toAbsolutePath();
        while (candidate != null && !Files.isDirectory(candidate.resolve("apps"))) {
            candidate = candidate.getParent();
        }
        if (candidate == null) throw new IllegalStateException("Cannot locate repository root");
        return candidate;
    }
}
