package greeter;

import static org.junit.Assert.assertEquals;
import greeter.api.Greeter;

import java.io.File;

import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleUnloader;
import org.junit.Test;

public class Test2_UnloadModuleHowTo {

	@Test
	public void test() throws Exception {
		// quote from https://jboss-modules.github.io/jboss-modules/manual/#introduction
		//   "And, they may be unloaded by the user at any time."
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo") })) {
			
			// load module
			Module module = moduleLoader.loadModule("greeter.english");
			
			// use module
			ModuleClassLoader classLoader = module.getClassLoader();
			Greeter englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			assertEquals("Hello World!", englishGreeter.sayHello("World"));
			
			// how do we unload a module without this ModuleUnloader hack?
			ModuleUnloader.unload(moduleLoader, module);
		}
	}

}
