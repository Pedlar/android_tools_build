/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.build.gradle.tasks

import com.android.build.gradle.internal.LintGradleClient
import com.android.tools.lint.HtmlReporter
import com.android.tools.lint.LintCliFlags
import com.android.tools.lint.Reporter
import com.android.tools.lint.XmlReporter
import com.android.tools.lint.checks.BuiltinIssueRegistry
import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.LintUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

public class Lint extends DefaultTask {

    private LintCliFlags flags = new LintCliFlags()
    private LintGradleClient client = new LintGradleClient(flags)
    private IssueRegistry registry = new BuiltinIssueRegistry()

    public void setQuiet() {
        flags.setQuiet(true)
    }

    public void setConfig(File f) {
        flags.setDefaultConfiguration(client.createConfigurationFromFile(f))
    }

    public void setHtmlOutput(File output) {
        output = output.getAbsoluteFile()
        if (output.exists()) {
            boolean delete = output.delete()
            if (!delete) {
                throw new GradleException("Could not delete old " + output)
            }
        }
        if (output.getParentFile() != null && !output.getParentFile().canWrite()) {
            throw new GradleException("Cannot write HTML output file " + output)
        }
        try {
            flags.getReporters().add(new HtmlReporter(client, output))
        } catch (IOException e) {
            throw new GradleException("HTML invalid argument.")
        }
    }

    public void setXmlOutput(File output) {
        output = output.getAbsoluteFile()
        if (output.exists()) {
            boolean delete = output.delete();
            if (!delete) {
                throw new GradleException("Could not delete old " + output)
            }
        }
        if (output.getParentFile() != null && !output.getParentFile().canWrite()) {
            throw new GradleException("Cannot write XML output file " + output)
        }
        try {
            flags.getReporters().add(new XmlReporter(client, output))
        } catch (IOException e) {
            throw new GradleException("XML invalid argument.")
        }
    }

    /**
     * Adds all files in sourceSets as a source file for lint.
     *
     * @param sourceSets files to be added to sources.
     */
    public void setSources(List<Set<File>> sourceSets) {
        for (Set<File> args : sourceSets) {
            for (File input : args) {
                if (input.exists()) {
                    List<File> sources = flags.getSourcesOverride()
                    if (sources == null) {
                        sources = new ArrayList<File>()
                        flags.setSourcesOverride(sources)
                    }
                    sources.add(input)
                }
            }
        }
    }

    /**
     * Adds all class files in directory specified by paths for lint.
     *
     * @param paths The absolute path to a directory containing class files
     */
    public void setClasspath(String paths) {
        for (String path : LintUtils.splitPath(paths)) {
            File input = new File(path);
            if (!input.exists()) {
                throw new GradleException("Class path entry " + input + " does not exist.")
            }
            List<File> classes = flags.getClassesOverride();
            if (classes == null) {
                classes = new ArrayList<File>();
                flags.setClassesOverride(classes);
            }
            classes.add(input);
        }
    }

    /**
     *  Adds all files in resourceSets as a resource file for lint.
     *
     * @param resourceSets files to be added to resources.
     */
    public void setLintResources(List<Set<File>> resourceSets) {
        for (Set<File> args : resourceSets) {
            for (File input : args) {
                if (input.exists()) {
                    List<File> resources = flags.getResourcesOverride()
                    if (resources == null) {
                        resources = new ArrayList<File>()
                        flags.setResourcesOverride(resources)
                    }
                    resources.add(input)
                }
            }
        }
    }

    @TaskAction
    public void lint() {
        List<Reporter> reporters = flags.getReporters()
        if (reporters.isEmpty()) {
            throw new GradleException("No reporter specified.")
        }

        Map<String, String> map = new HashMap<String, String>(){{
            put("", "file://")
        }}
        for (Reporter reporter : reporters) {
            reporter.setUrlMap(map)
        }

        try {
            client.run(registry, Arrays.asList(project.projectDir));
        } catch (IOException e) {
            throw new GradleException("Invalid arguments.")
        }
    }
}
