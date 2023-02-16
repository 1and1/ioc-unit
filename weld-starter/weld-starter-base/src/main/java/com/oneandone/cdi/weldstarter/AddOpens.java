package com.oneandone.cdi.weldstarter;

/*
 * Copyright 2021 Stefan Zobel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * A utility class that allows to open arbitrary packages to the calling module
 * at runtime, so it is a kind of dynamic device for "--add-opens" that could be
 * used inside libraries instead of forcing the application to be run with
 * command line parameters like "--add-opens java.base/java.util=ALL-UNNAMED" or
 * having the "Add-Opens:" entries supplied in the application Jar manifest.
 * Note that this still works in the Java 17 GA release, dated 2021-09-14 but it
 * may break at any time in the future (theoretically even for a minor
 * release!).
 */
public final class AddOpens {

    /**
     * Open one or more packages in the given module to the current module. Example
     * usage:
     *
     * <pre>{@code
     *  boolean success = AddOpens.open("java.base", "java.util", "java.net");
     * }</pre>
     *
     * @param moduleName   the module you want to open
     * @param packageNames packages in that module you want to be opened
     * @return {@code true} if the open operation has succeeded for all packages,
     * otherwise {@code false}
     */
    public static boolean open(String moduleName, String... packageNames) {
        // Use reflection so that this code can run on Java 8
        Class<?> javaLangModule;
        try {
            javaLangModule = Class.forName("java.lang.Module");
        } catch (Throwable t) {
            // we must be on Java 8
            return true;
        }
        try {
            // the module we are currently running in (either named or unnamed)
            Object thisModule = getCurrentModule();
            // find the module to open
            Object targetModule = findModule(moduleName);
            // get the method that is also used by "--add-opens"
            Method m = javaLangModule.getDeclaredMethod("implAddOpens", String.class, javaLangModule);
            // override language-level access checks
            setAccessible(m);
            // open given packages in the target module to this module
            for (String package_ : packageNames) {
                m.invoke(targetModule, package_, thisModule);
            }
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private static Object findModule(String moduleName) {
        // Use reflection so that this code can run on Java 8
        try {
            Class<?> moduleLayerClass = Class.forName("java.lang.ModuleLayer");
            Method bootMethod = moduleLayerClass.getDeclaredMethod("boot");
            Object bootLayer = bootMethod.invoke(null);
            Method findModuleMethod = moduleLayerClass.getDeclaredMethod("findModule", String.class);
            Optional<?> module = (Optional<?>) findModuleMethod.invoke(bootLayer, moduleName);
            return module.get();
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object getCurrentModule() {
        // Use reflection so that this code can run on Java 8
        try {
            Method m = Class.class.getDeclaredMethod("getModule");
            setAccessible(m);
            return m.invoke(AddOpens.class);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object getUnsafe(){
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field singleoneInstanceField = unsafeClass.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            putBooleanMethod = unsafeClass.getDeclaredMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
            return  singleoneInstanceField.get(null);
//             return unsafeClass.getDeclaredMethod("putBoolean");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void setAccessible(Method method) {
        try {
            putBooleanMethod.invoke(U, method, OVERRIDE_OFFSET, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        // if(U != null) {
           //  U.putBoolean(method, OVERRIDE_OFFSET, true);
        // }
    }

    // field offset of the override field (Warning: this may change at any time!)
    private static final long OVERRIDE_OFFSET = 12;
    private static final Object U = getUnsafe();

    private static Method putBooleanMethod;

    private AddOpens() {
        throw new AssertionError();
    }
}