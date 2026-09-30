package it.jui.input;

import org.junit.jupiter.api.Test;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class WidgetSpecTest {
    @Test void validatesScalarsActionsAndAllowedOptions() {
        assertEquals("", WidgetSpec.text().validate(""));
        assertEquals(42L, WidgetSpec.integer(0, 100).validate(42.0));
        assertEquals(true, WidgetSpec.action().validate(true));
        assertEquals(false, WidgetSpec.bool().validate(false));
        assertEquals("Italy", WidgetSpec.choice(List.of("Italy")).validate("Italy"));
        assertEquals(List.of("Java"), WidgetSpec.multiple(List.of("Java", "AI")).validate(List.of("Java")));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.text().validate(Map.of()));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.bool().validate("true"));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.action().validate(false));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.integer(0, 100).validate(42.5));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.integer(0, 100).validate(101));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.integer(0, 100).validate(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.choice(List.of("Italy")).validate("France"));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.multiple(List.of("Java")).validate(List.of("AI")));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.text().validate(null));
    }

    @Test void validatesDatesColorsAndMapCoordinates() {
        assertEquals("2026-09-30", WidgetSpec.date().validate("2026-09-30"));
        assertEquals("", WidgetSpec.date().validate(""));
        assertEquals("#abcdef", WidgetSpec.color().validate("#abcdef"));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.date().validate("2026-02-30"));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.color().validate("red"));
        var valid = Map.of("latitude", 40.0, "longitude", 14.0, "zoom", 10);
        assertNotNull(WidgetSpec.map().validate(valid));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.map().validate(Map.of("latitude", 91, "longitude", 0, "zoom", 1)));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.map().validate(Map.of("latitude", 0, "longitude", 0, "zoom", 1.5)));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.map().validate(Map.of()));
    }

    @Test void checksDecodedUploadSizeRatherThanTrustingMetadata() {
        String base64 = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3});
        assertNotNull(WidgetSpec.upload().validate(file(3, base64)));
        assertNotNull(WidgetSpec.upload().validate(file(0, "")));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.upload().validate(file(1, base64)));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.upload().validate(file(3, "!!!")));
        assertThrows(IllegalArgumentException.class, () -> WidgetSpec.upload().validate(file(5 * 1024 * 1024 + 1, "")));
        String maximum = Base64.getEncoder().encodeToString(new byte[5 * 1024 * 1024]);
        assertNotNull(WidgetSpec.upload().validate(file(5 * 1024 * 1024, maximum)));
    }

    private Map<String, Object> file(long size, String base64) {
        return Map.of("name", "file.bin", "contentType", "application/octet-stream", "size", size, "base64", base64);
    }
}
