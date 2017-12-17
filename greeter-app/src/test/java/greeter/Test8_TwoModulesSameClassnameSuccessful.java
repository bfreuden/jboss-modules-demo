package greeter;

import static org.junit.Assert.assertEquals;
import greeter.api.Greeter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test8_TwoModulesSameClassnameSuccessful {
	
	@BeforeClass
	public static void copyRepo() throws Exception {
		FileUtils.deleteQuietly(new File("repo_copy"));
		FileUtils.copyDirectory(new File("repo"), new File("repo_copy"));
	}
	
	@Test
	public void test() throws Exception {
		
		// create a second module (greeter-impl2.jar instead of greeter-impl.jar)
		FileUtils.copyDirectory(new File("repo/greeter/english"), new File("repo_copy/greeter/english2"));
		File moduleXmlFile = new File("repo_copy/greeter/english2/module.xml");
		String moduleXml = FileUtils.readFileToString(moduleXmlFile, "UTF-8");
		moduleXml = moduleXml.replace("greeter-impl", "greeter-impl2");
		moduleXml = moduleXml.replace("greeter.english", "greeter.english2");
		FileUtils.write(moduleXmlFile, moduleXml, "UTF-8");
		
		try (LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] { new File("repo_copy") })) {
			
			// two modules with same class name
			String sameClassName = "greeter.impl.EnglishGreeter";
			
			// load and use the first module
			Module module1 = moduleLoader.loadModule("greeter.english");
			ModuleClassLoader classLoader1 = module1.getClassLoader();
			Greeter englishGreeter1 = (Greeter)classLoader1.loadClass(sameClassName).newInstance();
			assertEquals("Hello World!", englishGreeter1.sayHello("World"));

			// load and use the second module
			Module module2 = moduleLoader.loadModule("greeter.english2");
			ModuleClassLoader classLoader2 = module2.getClassLoader();
			Greeter englishGreeter2 = (Greeter)classLoader2.loadClass(sameClassName).newInstance();
			assertEquals("Good morning World!", englishGreeter2.sayHello("World"));

		}
	}
}
