package io.fntlv.synchub.data.mod;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public class ModDataAPIInvoker {

    private static final String MOD_DATA_API_CLASS_NAME = "io.fntlv.moddataserializer.api.ModDataAPI";

    public static Optional<String> getPlayerModData(UUID uuid) {
        return (Optional<String>) invokeMethod("getPlayerModData", uuid);
    }

    public static void loadPlayerModData(UUID uuid, String serializedData) {
        invokeMethod("loadPlayerModData", uuid, serializedData);
    }

    private static Object invokeMethod(String methodName, Object... args) {
        try {
            Class<?> modDataAPIClass = Class.forName(MOD_DATA_API_CLASS_NAME);
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            Method method = modDataAPIClass.getMethod(methodName, argTypes);
            return method.invoke(null, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getModDataApiClassName() {
        return MOD_DATA_API_CLASS_NAME;
    }
}
