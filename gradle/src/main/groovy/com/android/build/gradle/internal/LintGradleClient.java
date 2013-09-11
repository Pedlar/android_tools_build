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

package com.android.build.gradle.internal;

import com.google.common.collect.Lists;

import com.android.annotations.NonNull;
import com.android.tools.lint.LintCliClient;
import com.android.tools.lint.LintCliFlags;
import com.android.tools.lint.detector.api.Project;

import java.io.File;
import java.util.List;

public class LintGradleClient extends LintCliClient {
    private List<File> mCustomRules = Lists.newArrayList();

    public LintGradleClient(LintCliFlags flags) {
        super(flags);
    }

    public void setCustomRules(List<File> customRules) {
        mCustomRules = customRules;
    }

    @Override
    public List<File> findRuleJars(@NonNull Project project) {
        return mCustomRules;
    }

    @Override
    protected Project createProject(@NonNull File dir, @NonNull File referenceDir) {
        return new LintGradleProject(this, dir, referenceDir);
    }
}
