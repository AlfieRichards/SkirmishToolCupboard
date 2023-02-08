package me.skirmish.handlers;

import me.skirmish.SkirmishTC;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Objects;

/**
 * Represents a configuration that automatically loads the default values from the plugin's resources, if it exists.
 * If no matching resource is found, the default configuration is empty. Automatically creates the file in the
 * plugin's datafolder if it doesn't exist, otherwise it loads the already saved file.
 */
public class Config extends YamlConfiguration {

    private final String filename;
    private final File file;

    /**
     * Creates a new config with the given filename.
     */
    public Config(final String filename) {
        this.filename = filename;
        file = new File(SkirmishTC.plugin.getDataFolder(), filename);
        loadDefaults();
        reload();
    }

    private void loadDefaults() {
        final YamlConfiguration defaultConfig = new YamlConfiguration();

        try (final InputStream inputStream = SkirmishTC.plugin.getResource(filename)) {
            if (inputStream != null) {
                try (final Reader reader = new InputStreamReader(Objects.requireNonNull(inputStream))) {
                    defaultConfig.load(reader);
                }
            }
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Could not load included config file " + filename, exception);
        } catch (final InvalidConfigurationException exception) {
            throw new IllegalArgumentException("Invalid default config for " + filename, exception);
        }

        setDefaults(defaultConfig);
    }

    /**
     * Reloads the configuration
     */
    public void reload() {
        saveDefaultConfig();
        try {
            load(file);
        } catch (final IOException exception) {
            new IllegalArgumentException("Could not find or load file " + filename, exception).printStackTrace();
        } catch (final InvalidConfigurationException exception) {
            SkirmishTC.plugin.getLogger().severe("Your config file " + filename + " is invalid, using default values now. Please fix the below mentioned errors and try again:");
            exception.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new UncheckedIOException(new IOException("Could not create directory " + parent.getAbsolutePath()));

            }
            SkirmishTC.plugin.saveResource(filename, false);
        }
    }

    /**
     * Saves the configuration under its original file name
     *
     * @throws IOException if the underlying YamlConfiguration throws it
     */
    public void save() throws IOException {
        this.save(file);
    }

}