package com.android.build.gradle.internal;

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.LintClient;
import com.android.tools.lint.detector.api.Project;

import java.io.File;
import java.util.Collections;

public class LintGradleProject extends Project {
    LintGradleProject(
            @NonNull LintClient client,
            @NonNull File dir,
            @NonNull File referenceDir) {
        super(client, dir, referenceDir);

        mGradleProject = true;
        mMergeManifests = true;
        mDirectLibraries = Collections.emptyList();
    }

    @Override
    protected void initialize() {
        // Deliberately not calling super; that code is for ADT compatibility
    }
}
