/*
 * Copyright (c) 2021, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.debugadapter.utils;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import io.ballerina.projects.Document;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.SingleFileProject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.ballerina.runtime.api.utils.IdentifierUtils.decodeIdentifier;
import static org.ballerinalang.debugadapter.utils.PackageUtils.MODULE_DIR_NAME;
import static org.ballerinalang.debugadapter.utils.PackageUtils.getDefaultModuleName;
import static org.ballerinalang.debugadapter.utils.PackageUtils.getOrgName;

/**
 * Concrete implementation of Ballerina project(package) source resolver.
 *
 * @since 2.0.0
 */
public class ProjectSourceResolver extends SourceResolver {

    ProjectSourceResolver(Project sourceProject) {
        super(sourceProject);
    }

    @Override
    public boolean isSupported(Location location) {
        try {
            if (sourceProject instanceof SingleFileProject) {
                DocumentId docId = sourceProject.currentPackage().getDefaultModule().documentIds().iterator().next();
                Document document = sourceProject.currentPackage().getDefaultModule().document(docId);
                return document.name().equals(location.sourcePath()) && document.name().equals(location.sourceName());
            } else if (sourceProject instanceof BuildProject) {
                String projectOrg = getOrgName(sourceProject);
                LocationInfo locationInfo = new LocationInfo(location);
                return locationInfo.isValid() && locationInfo.orgName().equals(projectOrg);
            } else {
                return false;
            }
        } catch (AbsentInformationException e) {
            return false;
        }
    }

    @Override
    public Optional<Path> resolve(Location location) {
        try {
            String projectRoot = sourceProject.sourceRoot().toAbsolutePath().toString();

            if (sourceProject instanceof SingleFileProject) {
                DocumentId docId = sourceProject.currentPackage().getDefaultModule().documentIds().iterator().next();
                Document document = sourceProject.currentPackage().getDefaultModule().document(docId);
                if (!document.name().equals(location.sourcePath()) || !document.name().equals(location.sourceName())) {
                    return Optional.empty();
                }
                return Optional.of(Paths.get(projectRoot));
            } else if (sourceProject instanceof BuildProject) {
                String projectOrg = getOrgName(sourceProject);
                String defaultModuleName = getDefaultModuleName(sourceProject);
                String locationName = location.sourceName();
                LocationInfo locationInfo = new LocationInfo(location);

                if (!locationInfo.isValid() || !locationInfo.orgName().equals(projectOrg)) {
                    return Optional.empty();
                }

                String modulePart = decodeIdentifier(locationInfo.moduleName());
                modulePart = modulePart.replaceFirst(defaultModuleName, "");
                if (modulePart.startsWith(".")) {
                    modulePart = modulePart.replaceFirst("\\.", "");
                }

                if (modulePart.isBlank()) {
                    // default module
                    return Optional.of(Paths.get(projectRoot, locationName));
                } else {
                    // other modules
                    return Optional.of(Paths.get(projectRoot, MODULE_DIR_NAME, modulePart, locationName));
                }
            }
            return Optional.empty();
        } catch (AbsentInformationException e) {
            return Optional.empty();
        }
    }
}
