package io.fntlv.synchub.core.libs;

import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.dependencies.DependencyManager;
import net.byteflux.libby.Library;

public class SyncHubLib {

    public static void init(){
        DependencyManager dependencyManager = EverNifeCore.getDependencyManager();
        dependencyManager.loadLibrary(
                Library.builder()
                        .groupId("com.zaxxer")
                        .artifactId("HikariCP")
                        .version("4.0.3")
                        .build()
        );
    }

}
