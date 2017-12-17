package greeter;

import static org.junit.Assert.assertEquals;
import greeter.api.Greeter;

import java.io.File;
import java.util.ServiceLoader;

import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.junit.Test;

public class Test1_VanillaSuccessful {
	
	@Test
	public void test() throws Exception {
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo") })) {
			
			// load module
			Module module = moduleLoader.loadModule("greeter.english");

			// get instance by class by name
			ModuleClassLoader classLoader = module.getClassLoader();
			Greeter englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			assertEquals("Hello World!", englishGreeter.sayHello("World"));
			
			// get instance by SPI
			ServiceLoader<Greeter> greeters = module.loadService(Greeter.class);
			int loops = 0;
			for (Greeter greeter : greeters) {
				loops++;
				assertEquals("Hello World!", greeter.sayHello("World"));
			}
			assertEquals("module not found by ServiceLoader", 1, loops);
		}
	}
	
}
