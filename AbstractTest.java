package easysh2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * N'importe quelle classe qui �tendra cette classe pourra appeler une m�thode
 * <CODE>test</CODE> qui ex�cutera toutes les m�thodes public dont le nom
 * commence par "test".<BR>
 * On impl�mente l'interface Test pour que, par h�ritage, les classes h�rit�es
 * puissent �tre appel�es par RunAllTests
 */
public abstract class AbstractTest implements Test {
	protected final Logger log;

	public AbstractTest(String className) {
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
					log.warning(methodName + ": " + e.getCause());
				}
				log.info(methodName + " OK");
			}
		}
	}

}
