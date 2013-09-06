package com.android.builder.internal;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BuildConfigGeneratorTest extends TestCase {
    public void testFalse() throws Exception {
        File tempDir = Files.createTempDir();
        BuildConfigGenerator generator = new BuildConfigGenerator(tempDir.getPath(),
                "my.app.pkg", false);
        generator.generate(Collections.<String>emptyList());
        File file = generator.getBuildConfigFile();
        assertTrue(file.exists());
        String actual = Files.toString(file, Charsets.UTF_8);
        assertEquals(
                "/** Automatically generated file. DO NOT MODIFY */\n"
                + "package my.app.pkg;\n"
                + "\n"
                + "public final class BuildConfig {\n"
                + "    public static final boolean DEBUG = false;\n"
                + "\n"
                + "}", actual);
        file.delete();
        tempDir.delete();
    }

    public void testTrue() throws Exception {
        File tempDir = Files.createTempDir();
        BuildConfigGenerator generator = new BuildConfigGenerator(tempDir.getPath(),
                "my.app.pkg", true);
        generator.generate(Collections.<String>emptyList());
        File file = generator.getBuildConfigFile();
        assertTrue(file.exists());
        String actual = Files.toString(file, Charsets.UTF_8);
        assertEquals(
                "/** Automatically generated file. DO NOT MODIFY */\n"
                + "package my.app.pkg;\n"
                + "\n"
                + "public final class BuildConfig {\n"
                + "    public static final boolean DEBUG = Boolean.parseBoolean(\"true\");\n"
                + "\n"
                + "}", actual);
        file.delete();
        tempDir.delete();
    }

    public void testExtra() throws Exception {
        File tempDir = Files.createTempDir();
        BuildConfigGenerator generator = new BuildConfigGenerator(tempDir.getPath(),
                "my.app.pkg", false);
        generator.generate(Arrays.asList("// Extra line:", "public static int EXTRA = 42;"));
        File file = generator.getBuildConfigFile();
        assertTrue(file.exists());
        String actual = Files.toString(file, Charsets.UTF_8);
        assertEquals(
                "/** Automatically generated file. DO NOT MODIFY */\n"
                + "package my.app.pkg;\n"
                + "\n"
                + "public final class BuildConfig {\n"
                + "    public static final boolean DEBUG = false;\n"
                + "\n"
                + "    // Extra line:\n"
                + "    public static int EXTRA = 42;\n"
                + "}", actual);
        file.delete();
        tempDir.delete();
    }
}
