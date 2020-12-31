package easysh2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * N'importe quelle classe qui étendra cette classe pourra appeler une méthode
 * <CODE>test</CODE> qui exécutera toutes les méthodes public dont le nom
 * commence par "test".<BR>
 * On implémente l'interface Test pour que, par héritage, les classes héritées
 * puissent être appelées par RunAllTests
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
