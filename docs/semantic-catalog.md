# Semantic catalog

JUI models its public UI surface as capabilities rather than isolated Java methods. The built-in catalog is packaged in `jui-core` at `META-INF/jui/capabilities.json`.

Each capability declares:

- a stable language-independent `id`;
- the application intent it implements;
- its interaction model;
- the `ui.*` methods that implement it;
- example user intents in English and Italian;
- related capabilities useful for composition.

For example, `data.crud` represents the intent to manage typed entities and maps to every `ui.crud(...)` overload. Overloads remain implementation choices; the model first chooses the semantic capability.

Every application under `apps/` packages its own `META-INF/jui/application.json`. An application profile describes an archetype, the user requests it can satisfy and the capabilities used to implement it.

## Runtime access

```java
SemanticCatalog catalog = SemanticCatalog.builtIn();

CapabilityDefinition crud = catalog.require("data.crud");

String promptContext = catalog.compactJson(Set.of(
        "data.crud",
        "form.typed",
        "layout.metric"));
```

`compactJson` is intended for retrieval pipelines: a local model receives only the capabilities relevant to the current request rather than the whole JUI API.

## Build guarantees

The test suite checks that:

- every public generation-facing `ui.*` method belongs to a capability;
- the catalog contains no unknown methods or related capability IDs;
- every example application has a valid semantic profile;
- every application capability references the built-in catalog.

Adding a public UI method without cataloging its meaning therefore fails the build.
