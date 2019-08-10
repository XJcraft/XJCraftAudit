package org.jim.bukkit.audit.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

public abstract class Storeable implements Serializable {

    public void save(File file) throws IOException {
        YamlConfiguration yaml =
                (YamlConfiguration) serial(new YamlConfiguration());
        if (yaml != null)
            yaml.save(file);

    }

    public void load(File file) throws FileNotFoundException, IOException,
            InvalidConfigurationException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(file);
        unSerial(yaml);
    }

    public abstract MemorySection serial(MemorySection yaml);

    public abstract void unSerial(MemorySection yaml);
}
