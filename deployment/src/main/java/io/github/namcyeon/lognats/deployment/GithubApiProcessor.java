package io.github.namcyeon.lognats.deployment;

import static io.github.namcyeon.lognats.deployment.GitHubApiDotNames.GH_ROOT_OBJECTS;
import static io.github.namcyeon.lognats.deployment.GitHubApiDotNames.GH_SIMPLE_OBJECTS;

import org.jboss.jandex.DotName;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class GithubApiProcessor {

    private static final String FEATURE = "github-api";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ExtensionSslNativeSupportBuildItem requireSsl() {
        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    }

    @BuildStep
    IndexDependencyBuildItem indexGitHubApiJar() {
        return new IndexDependencyBuildItem("org.kohsuke", "github-api");
    }

    @BuildStep
    void registerForReflection(CombinedIndexBuildItem combinedIndex,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        for (DotName rootModelObject : GH_ROOT_OBJECTS) {
            reflectiveClasses.produce(new ReflectiveClassBuildItem(true, true, rootModelObject.toString()));

            reflectiveClasses.produce(new ReflectiveClassBuildItem(true, true,
                    combinedIndex.getIndex().getAllKnownSubclasses(rootModelObject).stream()
                            .map(ci -> ci.name().toString())
                            .toArray(String[]::new)));
        }

        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, true,
                GH_SIMPLE_OBJECTS.stream().map(DotName::toString).toArray(String[]::new)));
    }
}
