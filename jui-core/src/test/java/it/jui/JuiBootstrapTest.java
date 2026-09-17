package it.jui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JuiBootstrapTest {

    @Test
    void rejectsNullApplication() {
        assertThrows(NullPointerException.class, () -> Jui.run(null));
    }
}
