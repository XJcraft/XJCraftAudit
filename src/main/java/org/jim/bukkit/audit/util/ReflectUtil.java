package org.jim.bukkit.audit.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {

	public static Object getValue(Object obj, String field) throws Exception {
		Field f = obj.getClass().getDeclaredField(field);
		if (!f.isAccessible())
			f.setAccessible(true);
		return f.get(obj);
	}

	public static void setValue(Object obj, String field, Object value)
			throws Exception {
		Field f = obj.getClass().getDeclaredField(field);
		if (!f.isAccessible())
			f.setAccessible(true);
		f.set(obj, value);
	}

	public static Object invokeMethod(Object obj, String method, Object... args)
			throws Exception {
		Class<?>[] parameterTypes = new Class<?>[args.length];
		if (args.length > 0)
			for (int i = 0; i < args.length; i++)
				parameterTypes[i] = args[i].getClass();
		Method m = obj.getClass().getMethod(method, parameterTypes);
		if (!m.isAccessible())
			m.setAccessible(true);
		return m.invoke(obj, args);
	}
}
