package greeter;

import static org.junit.Assert.assertEquals;
import greeter.api.Greeter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleUnloader;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test3_UnloadAndDeleteModuleError {
	
	@BeforeClass
	public static void copyRepo() throws Exception {
		FileUtils.deleteQuietly(new File("repo_copy"));
		FileUtils.copyDirectory(new File("repo"), new File("repo_copy"));
	}
	
	@Test
	public void test() throws Exception {
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo_copy") })) {
			
			// load and use module
			Module module = moduleLoader.loadModule("greeter.english");
			ModuleClassLoader classLoader = module.getClassLoader();
			Greeter englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			assertEquals("Hello World!", englishGreeter.sayHello("World"));
			
			// unload module with the hack...
			ModuleUnloader.unload(moduleLoader, module);
			System.gc();
			// ... it is not possible to remove the directory because the module jar is still locked by JBoss Modules
			FileUtils.deleteDirectory(new File("repo_copy/greeter/english"));
		}
	}
}

// Using http://file-leak-detector.kohsuke.org/ we can get this stack
//     JVM args example: -javaagent:C:\Java\file-leak-detector-1.8-jar-with-dependencies.jar=http=19999
//			#4 repo_copy\greeter\english\lib\greeter-impl-1.0-SNAPSHOT.jar by thread:main on Sun Dec 17 15:56:57 CET 2017
//			at java.util.zip.ZipFile.<init>(Unknown Source)
//			at java.util.jar.JarFile.<init>(Unknown Source)
//			at java.util.jar.JarFile.<init>(Unknown Source)
//			at org.jboss.modules.xml.JDKSpecific.getJarFile(JDKSpecific.java:33)
//			at org.jboss.modules.xml.ModuleXmlParser$DefaultResourceRootFactory.createResourceLoader(ModuleXmlParser.java:1385)
//			at org.jboss.modules.LocalModuleFinder.lambda$new$0(LocalModuleFinder.java:94)
//			at org.jboss.modules.xml.ModuleXmlParser.parseResourceRoot(ModuleXmlParser.java:1009)
//			at org.jboss.modules.xml.ModuleXmlParser.parseResources(ModuleXmlParser.java:855)
//			at org.jboss.modules.xml.ModuleXmlParser.parseModuleContents(ModuleXmlParser.java:649)
//			at org.jboss.modules.xml.ModuleXmlParser.parseDocument(ModuleXmlParser.java:418)
//			at org.jboss.modules.xml.ModuleXmlParser.parseModuleXml(ModuleXmlParser.java:298)
//			at org.jboss.modules.xml.ModuleXmlParser.parseModuleXml(ModuleXmlParser.java:256)
//			at org.jboss.modules.xml.ModuleXmlParser.parseModuleXml(ModuleXmlParser.java:217)
//			at org.jboss.modules.LocalModuleFinder.parseModuleXmlFile(LocalModuleFinder.java:256)
//			at org.jboss.modules.LocalModuleFinder.lambda$findModule$1(LocalModuleFinder.java:199)
//			at java.security.AccessController.doPrivileged(Native Method)
//			at org.jboss.modules.LocalModuleFinder.findModule(LocalModuleFinder.java:199)
//			at org.jboss.modules.ModuleLoader.findModule0(ModuleLoader.java:693)
//			at org.jboss.modules.ModuleLoader.findModule(ModuleLoader.java:686)
//			at org.jboss.modules.ModuleLoader.loadModuleLocal(ModuleLoader.java:496)
//			at org.jboss.modules.ModuleLoader.preloadModule(ModuleLoader.java:399)
//			at org.jboss.modules.ModuleLoader.loadModule(ModuleLoader.java:292)
//			at greeter.Test3_UnloadAndDeleteModuleError.test(Test3_UnloadAndDeleteModuleError.java:26)
