package easysh2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

public abstract class Test {
	protected final Logger log;

	public Test(String className) {
		log = new MyLogger(className, Logger.getAnonymousLogger().getResourceBundleName());
		boolean assertEnabled = false;
		assert assertEnabled = true;
		if (!assertEnabled) {
			throw new AssertionError("Lancer avec l'argument VM -ea");
		}
	}

	public final void test() {
		for (Method method : this.getClass().getDeclaredMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith("test") && !methodName.equals("test")) {
				try {
					method.invoke(this, new Object[] {});
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					log.warning(method.getName() + ": " + e.getCause());
				}
			}
		}
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @see https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
	 */
	protected static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base
	 *                    directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				assert !fileName.contains(".");
				classes.addAll(findClasses(file, packageName + "." + fileName));
			} else if (fileName.endsWith("Test.class") && !fileName.equals("Test.class")) {
				String className = fileName.substring(0, fileName.length() - 6);
				Class<?> _class = Class.forName(packageName + '.' + className);
				classes.add(_class);
			}
		}
		return classes;
	}

	public static void main(String[] args) {
		Class[] classes = null;
		try {
			classes = getClasses(Test.class.getPackage().getName());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Class _class : classes) {
			try {
				Test newInstance = (Test) _class.newInstance();
				newInstance.test();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
	}

	public boolean equalsWithoutSign(short s1, short s2) {
		return s1 == s2 || (int) (s1 & 0x0000FFFF) == (int) (s2 & 0x0000FFFF);
	}

	protected void assertEqualsByteArray(byte[] b1, byte[] b2) {
		if (b1 == null && b2 == null) {
			return;
		}
		if (b1 == null && b2 != null || b1 != null && b2 == null) {
			throw new AssertionError("Arrays are not both null");
		}
		if (b1.length != b2.length) {
			throw new AssertionError("Sizes differ. b1.length: " + b1.length + " - b2.length: " + b2.length);
		}
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				throw new AssertionError("Value b1[" + i + "]: " + b1[i] + " - b2[" + i + "]: " + b2[i]);
			}
		}
	}

}
