package greeter;

import greeter.api.Greeter;

import java.io.File;
import java.util.ServiceLoader;

import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;

public class GreeterApp {

	public static void main(String[] args) throws Exception {
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo") })) {
			
			// load module
			Module module = moduleLoader.loadModule("greeter.english");

			// get instance by class by name
			ModuleClassLoader classLoader = module.getClassLoader();
			Greeter englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			System.out.println(englishGreeter.sayHello("World"));
			
			// get instance by SPI
			ServiceLoader<Greeter> greeters = module.loadService(Greeter.class);
			for (Greeter greeter : greeters) 
				System.out.println(greeter.sayHello("World"));
		}
	}
}
