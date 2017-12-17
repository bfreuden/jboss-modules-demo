package greeter;

import static org.junit.Assert.assertEquals;
import greeter.api.Greeter;

import java.io.File;
import java.util.ServiceLoader;

import org.apache.commons.io.FileUtils;
import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleUnloader;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test5_UnloadAndDeleteModuleError2 {
	
	@BeforeClass
	public static void copyRepo() throws Exception {
		FileUtils.deleteQuietly(new File("repo_copy"));
		FileUtils.copyDirectory(new File("repo"), new File("repo_copy"));
	}
	
	@Test
	public void test() throws Exception {
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo_copy") })) {
			
			// load the module
			Module module = moduleLoader.loadModule("greeter.english");
			ModuleClassLoader classLoader = module.getClassLoader();
			
			// use the module via ServiceLoader
			ServiceLoader<Greeter> greeters = module.loadService(Greeter.class);
			int loops = 0;
			for (Greeter greeter : greeters) {
				loops++;
				assertEquals("Hello World!", greeter.sayHello("World"));
			}
			assertEquals("module not found by ServiceLoader", 1, loops);
			
			// but even if we unload the module with the hacks...
			ModuleUnloader.unload(moduleLoader, module);
			classLoader.close();
			System.gc();
			
			// ... it is not possible to remove the directory because the module jar is still locked by ServiceLoader
			FileUtils.deleteDirectory(new File("repo_copy/greeter/english"));
		}
	}
}

// Using http://file-leak-detector.kohsuke.org/ we can get this stack
//    JVM args example: -javaagent:C:\Java\file-leak-detector-1.8-jar-with-dependencies.jar=http=19999
//			#4 C:\dev\Perso\Eclipse\modules-demo\greeter-app\repo_copy\greeter\english\lib\greeter-impl-1.0-SNAPSHOT.jar by thread:main on Sun Dec 17 15:51:36 CET 2017
//			at java.util.zip.ZipFile.<init>(Unknown Source)
//			at java.util.jar.JarFile.<init>(Unknown Source)
//			at java.util.jar.JarFile.<init>(Unknown Source)
//			at sun.net.www.protocol.jar.URLJarFile.<init>(Unknown Source)
//			at sun.net.www.protocol.jar.URLJarFile.getJarFile(Unknown Source)
//			at sun.net.www.protocol.jar.JarFileFactory.get(Unknown Source)
//			at sun.net.www.protocol.jar.JarURLConnection.connect(Unknown Source)
//			at sun.net.www.protocol.jar.JarURLConnection.getInputStream(Unknown Source)
//			at java.net.URL.openStream(Unknown Source)
//			at java.util.ServiceLoader.parse(Unknown Source)
//			at java.util.ServiceLoader.access$200(Unknown Source)
//			at java.util.ServiceLoader$LazyIterator.hasNextService(Unknown Source)
//			at java.util.ServiceLoader$LazyIterator.hasNext(Unknown Source)
//			at java.util.ServiceLoader$1.hasNext(Unknown Source)
//			at greeter.Test5_UnloadAndDeleteModuleError2.test(Test5_UnloadAndDeleteModuleError2.java:32)

// It seems to me that Lucene is doing a heavy use of ServiceLoader. To be checked.
