package org.yansou.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class ReflectUtils {
	/**
	 * 按名称调用方法，可以调用接口中没有显示的方法。
	 * 
	 * @param clazz
	 * @param methodName
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final static public <T> T callStatic(Class<?> clazz, String methodName, Object... args) {
		Method method = findMethod(clazz, methodName, argsTypes(args));
		return (T) invokeMethod(method, null, args);

	}

	@SuppressWarnings("unchecked")
	final static public <T> T call(Object obj, String methodName, Object... args) {
		Method method = findMethod(obj.getClass(), methodName, argsTypes(args));
		return (T) invokeMethod(method, obj, args);

	}

	static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	final static Class<?>[] argsTypes(Object... args) {
		Class<?>[] res = new Class<?>[args.length];
		int len = args.length;
		for (int i = 0; i < len; i++) {
			res[i] = args[i].getClass();
		}
		return res;
	}

	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (Method method : methods) {
				if (name.equals(method.getName())
						&& (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * 获得对象的字段内容。 可以按点分割，获得字段中的字段。 httpclient.context.value
	 * 
	 * @param object
	 * @param findNameLike
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Object object, String findNameLike) {
		Object thisObj = object;
		for (String name : StringUtils.split(findNameLike, '.')) {
			Class<?> clazz = thisObj.getClass();
			Field field = getAllFields0(clazz).get(name);
			if (Objects.nonNull(field)) {
				try {
					field.setAccessible(true);
					thisObj = field.get(thisObj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
			} else {
				throw new IllegalArgumentException("no such field.");
			}
		}
		return (T) thisObj;
	}

	/**
	 * 获得所有字段
	 * 
	 * @param clazz
	 * @return
	 */
	private static LinkedHashMap<String, Field> getAllFields0(Class<?> clazz) {
		LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();
		Class<?> thisClass = clazz;
		for (;;) {
			Field[] fs = thisClass.getDeclaredFields();
			Stream.of(fs).filter(f -> !fieldMap.containsKey(f.getName())).forEach(f -> fieldMap.put(f.getName(), f));
			thisClass = thisClass.getSuperclass();
			if (null == thisClass) {
				break;
			}
		}
		return fieldMap;
	}

	static public <T> T getProxy(Class<T> clazz, Object obj) {
		
		return null;
	}
}
