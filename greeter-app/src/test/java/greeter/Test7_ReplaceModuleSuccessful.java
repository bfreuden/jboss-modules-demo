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

public class Test7_ReplaceModuleSuccessful {
	
	@BeforeClass
	public static void copyRepo() throws Exception {
		FileUtils.deleteQuietly(new File("repo_copy"));
		FileUtils.copyDirectory(new File("repo"), new File("repo_copy"));
	}
	
	@Test
	public void test() throws Exception {
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo_copy") })) {
			
			// load and use the first version of the module
			Module module = moduleLoader.loadModule("greeter.english");
			ModuleClassLoader classLoader = module.getClassLoader();
			Greeter englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			assertEquals("Hello World!", englishGreeter.sayHello("World"));

			// unload the module (with the hacks)
			ModuleUnloader.unload(moduleLoader, module);
			classLoader.close();
			
			// delete the module
			FileUtils.deleteDirectory(new File("repo_copy/greeter/english"));
			
			// replace the module with another implementation (greeter-impl2.jar instead of greeter-impl.jar)
			FileUtils.copyDirectory(new File("repo/greeter/"), new File("repo_copy/greeter/"));
			File moduleXmlFile = new File("repo_copy/greeter/english/module.xml");
			String moduleXml = FileUtils.readFileToString(moduleXmlFile, "UTF-8");
			moduleXml = moduleXml.replace("greeter-impl", "greeter-impl2");
			FileUtils.write(moduleXmlFile, moduleXml, "UTF-8");
			
			// load and use the second version of the module (good morning instead of hello)
			module = moduleLoader.loadModule("greeter.english");
			classLoader = module.getClassLoader();
			englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			assertEquals("Good morning World!", englishGreeter.sayHello("World"));
			
			// unload module (with the hacks)
			ModuleUnloader.unload(moduleLoader, module);
			classLoader.close();
			
			// delete the module
			FileUtils.deleteDirectory(new File("repo_copy/greeter/english"));
		}
	}
}
