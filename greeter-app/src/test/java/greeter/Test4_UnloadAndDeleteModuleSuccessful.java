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

public class Test4_UnloadAndDeleteModuleSuccessful {
	
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
			
			// ... then use the close method of the hacked version of ModuleClassLoader...
			classLoader.close();
			
			// ... and it is possible to remove the directory :-)
			FileUtils.deleteDirectory(new File("repo_copy/greeter/english"));
		}
	}

}
