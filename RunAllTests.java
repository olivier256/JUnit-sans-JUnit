package easysh2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Toutes les classes dans le classPath dont le nom se termine par Test verront
 * appelée leur méthode <CODE>test</CODE>. Il vient que les classes doivent
 * implémentées l'interface <CODE>Test</CODE>
 */
public class RunAllTests {

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
			} else if (fileName.endsWith("Test.class") && !fileName.equals("AbstractTest.class")
					&& !fileName.equals("Test.class")) {
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
			classes = getClasses(AbstractTest.class.getPackage().getName());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Class _class : classes) {
			try {
				Test newInstance = (Test) _class.newInstance();
				newInstance.test();
			} catch (InstantiationException ) {
				System.err.println("Est-ce qu'il existe bien un constructeur SANS paramètre ?");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("Est-ce que le constructeur vide est bien public ?");
				e.printStackTrace();
			}

		}
	}

}
