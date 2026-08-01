package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LauncherTest {

    @Test
    void launcherClassExists() {
        assertNotNull(Launcher.class);
    }
}