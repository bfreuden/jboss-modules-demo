package greeter;

import static org.junit.Assert.assertEquals;
import greeter.api.Greeter;

import java.io.File;

import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.junit.Test;

public class Test6_SecurityManagerError {
	
	@Test
	public void test() throws Exception {
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo") })) {
			// Fails here with this exception
			//		Caused by: java.lang.SecurityException
			//			at org.jboss.modules.Module.getPrivateAccess(Module.java:163)
			//			at org.jboss.modules.security.ModularPermissionFactory.<clinit>(ModularPermissionFactory.java:41)
			Module module = moduleLoader.loadModule("greeter.english");
			ModuleClassLoader classLoader = module.getClassLoader();
			Greeter englishGreeter = (Greeter)classLoader.loadClass("greeter.impl.EnglishGreeter").newInstance();
			assertEquals("Hello World!", englishGreeter.sayHello("World"));
		}
	}
}
