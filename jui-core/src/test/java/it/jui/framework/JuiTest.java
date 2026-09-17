package it.jui.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JuiTest {

    @Test
    void rejectsNullApplication() {
        assertThrows(NullPointerException.class, () -> Jui.run(null));
    }
}
