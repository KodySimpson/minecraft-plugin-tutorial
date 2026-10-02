package dev.kodysimpson.launchpads;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class LaunchListenerTest {
    @Test void horizontalFacingAndUpwardStrength() {
        float[] yaws = {0, 90, 180, 270};
        double[] x = {0, -0.8, 0, 0.8};
        double[] z = {0.8, 0, -0.8, 0};
        for (int i = 0; i < yaws.length; i++) {
            var velocity = LaunchListener.launchVelocity(new Location(null, 0, 0, 0, yaws[i], 0), 0.8, 1.2);
            assertEquals(x[i], velocity.getX(), 1e-9);
            assertEquals(z[i], velocity.getZ(), 1e-9);
            assertEquals(1.2, velocity.getY(), 1e-9);
        }
    }

    @Test void pitchDoesNotChangeLaunchOrMutateInput() {
        for (float pitch : new float[]{-90, 0, 90}) {
            var location = new Location(null, 2, 3, 4, 45, pitch);
            var velocity = LaunchListener.launchVelocity(location, 0.8, 1);
            assertEquals(0.8, Math.hypot(velocity.getX(), velocity.getZ()), 1e-9);
            assertEquals(1, velocity.getY(), 1e-9);
            assertEquals(pitch, location.getPitch());
        }
    }

    @Test void zeroForwardLaunchesStraightUp() {
        var velocity = LaunchListener.launchVelocity(new Location(null, 0, 0, 0, 90, 90), 0, 1);
        assertEquals(0, velocity.getX(), 1e-9);
        assertEquals(0, velocity.getZ(), 1e-9);
        assertEquals(1, velocity.length(), 1e-9);
    }

    @Test void bundledConfigurationMatchesTheLesson() throws Exception {
        try (var input = getClass().getResourceAsStream("/config.yml")) {
            assertNotNull(input);
            var config = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
            assertTrue(config.getBoolean("launch-pad.enabled"));
            assertEquals("GOLD_BLOCK", config.getString("launch-pad.material"));
            assertEquals(1, config.getDouble("launch-pad.upward-strength"));
            assertEquals(1.6, config.getDouble("launch-pad.forward-strength"));
            assertEquals("<gold>Whoooosh!</gold>", config.getString("launch-pad.message"));
            assertEquals(5, config.getConfigurationSection("launch-pad").getKeys(false).size());
        }
    }
}
