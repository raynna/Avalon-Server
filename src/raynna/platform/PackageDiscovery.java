package raynna.platform;

import raynna.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PackageDiscovery {

    private PackageDiscovery() {
    }

    public static Class<?>[] getClasses(List<String> packageNames) {
        List<Class<?>> classes = new ArrayList<>();
        for (String packageName : packageNames) {
            try {
                Class<?>[] found = Utils.getClasses(packageName);
                if (found != null) {
                    for (Class<?> clazz : found) {
                        classes.add(clazz);
                    }
                }
            } catch (ClassNotFoundException | IOException ignored) {
                // Allows staged package migration where some target packages do not exist yet.
            }
        }
        return classes.toArray(new Class<?>[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T>[] getTypedClasses(List<String> packageNames) {
        return (Class<T>[]) getClasses(packageNames);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T>[] getTypedClasses(String packageName) throws IOException, ClassNotFoundException {
        return (Class<T>[]) Utils.getClasses(packageName);
    }
}
