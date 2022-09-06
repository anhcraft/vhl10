package dev.anhcraft.vhvl10.resources;

import dev.anhcraft.config.NeepConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.neep.NeepConfig;
import dev.anhcraft.neep.errors.NeepReaderException;
import dev.anhcraft.vhvl10.objects.UserData;

import java.io.File;
import java.io.IOException;

public class Storage {
    public static UserData userData;

    public static File getStorageFile() {
        File f = new File("storage");
        //noinspection ResultOfMethodCallIgnored
        f.mkdir();
        return f;
    }

    public static void loadUserData() {
        File file = new File(getStorageFile(), "user.neep");
        try {
            if (file.createNewFile()) {
                userData = new UserData();
            } else {
                userData = new VConfigProvider().createDeserializer().transform(UserData.class, SimpleForm.of(new NeepConfigSection(NeepConfig.fromFile(file).getRoot())));
            }
        } catch (IOException | NeepReaderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveUserData() {
        File file = new File(getStorageFile(), "user.neep");
        try {
            NeepConfig.of(((NeepConfigSection) new VConfigProvider().createSerializer().transform(UserData.class, userData).asSection()).getBackend()).save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
