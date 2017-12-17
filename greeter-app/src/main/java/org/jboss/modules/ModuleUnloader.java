package org.jboss.modules;

/**
 * Hack class to access ModuleLoader#unloadModuleLocal protected method 
 */
public class ModuleUnloader {
	
	public static void unload(ModuleLoader loader, Module module) {
		loader.unloadModuleLocal(module.getName(), module);
	}
	
}
