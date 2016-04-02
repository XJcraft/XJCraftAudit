package org.jim.bukkit.audit;

public class XJException extends IllegalArgumentException {

	public XJException(String message) {
		super(message);
	}

	public static void throwMe(String message) {
		throw new XJException(message);
	}
}
