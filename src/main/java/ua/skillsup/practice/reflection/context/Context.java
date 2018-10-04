package ua.skillsup.practice.reflection.context;

import ua.skillsup.practice.reflection.service.MainService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Context {

	private Map<Class<?>, Object> createdInstances;

	public Context(Class<?> ... classes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Map<Class<?>, Class<?>[]> classesToCreatePerConstructorArguments =
				createDefinitions(classes);
		createInstances(classesToCreatePerConstructorArguments);
	}

	private static Map<Class<?>, Class<?>[]> createDefinitions(Class<?> [] classes) {
		Map<Class<?>, Class<?>[]> classesToCreatePerConstructorArguments =
				new HashMap<>();
		for (Class<?> aClass : classes) {
			Constructor<?>[] constructors = aClass.getConstructors();
			if (constructors.length > 1) {
				throw new IllegalArgumentException("Can't support class with several " +
						"constructors -> " + aClass.getName());
			}
			classesToCreatePerConstructorArguments
					.put(aClass, constructors[0].getParameterTypes());
		}
		return classesToCreatePerConstructorArguments;
	}

	private void createInstances(Map<Class<?>, Class<?>[]> definitions) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		this.createdInstances = new HashMap<>();
		Map<Class<?>, Class<?>[]> notCreated = new HashMap<>(definitions);
		do {
			Iterator<Class<?>> iterator = notCreated.keySet().iterator();
			while (iterator.hasNext()) {
				Class<?> aClass = iterator.next();
				Class<?>[] constructorArgs = notCreated.get(aClass);
				boolean canBeCreated = true;
				for (Class<?> classToCheck : constructorArgs) {
					if (!createdInstances.containsKey(classToCheck)) {
						canBeCreated = false;
						break;
					}
				}
				if (canBeCreated) {
					Constructor<?> constructor = aClass.getConstructor(constructorArgs);
					Object[] args = new Object[constructorArgs.length];
					for (int i = 0; i < constructorArgs.length; i++) {
						args[i] = createdInstances.get(constructorArgs[i]);
					}
					Object instance = constructor.newInstance(args);
					createdInstances.put(aClass, instance);
					iterator.remove();
				}
			}
		} while (!notCreated.isEmpty());
	}

	public <T> T getBean(Class<T> aClass) {
		Class<?> aClass1 = createdInstances.keySet().stream()
				.filter(classToCheck -> acceptedClass(aClass, classToCheck))
				.findFirst()
				.orElseThrow(() -> new RuntimeException());
		return (T) createdInstances.get(aClass1);
	}

	private boolean acceptedClass(Class<?> target, Class<?> classToCheck) {
		if (target.equals(classToCheck)) {
			return true;
		}
		for (Class<?> anInterface : classToCheck.getInterfaces()) {
			if (target.equals(anInterface)) {
				return true;
			}
		}
		return false;
	}
}
