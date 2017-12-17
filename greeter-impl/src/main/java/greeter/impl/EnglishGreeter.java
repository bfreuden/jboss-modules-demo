package greeter.impl;

import greeter.api.Greeter;

import java.io.File;
import java.io.FileOutputStream;

public class EnglishGreeter implements Greeter {

	public String sayHello(String to) {
		createWriteDeleteTempFile();
		return "Hello " + to + "!";
	}

	private void createWriteDeleteTempFile() {
		try {
			File temp = File.createTempFile("foo", ".bar");
			try (FileOutputStream fos = new FileOutputStream(new File(System.getProperty("java.io.tmpdir") + "/test.txt"))) {
				fos.write((byte)'a');
			} finally {
				temp.delete();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
