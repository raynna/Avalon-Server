package com.rs.java.utils;

import java.lang.reflect.Field;

public final class Colors {

	public static String red = "<col=ff0000>";
	public static String green = "<col=00ff00>";
	public static String blue = "<col=0000ff>";
	public static String white = "<col=ffffff>";
	public static String black = "<col=000000>";
	public static String orange = "<col=C9A204>";
	public static String cyan = "<col=00B2ED>";
	public static String gray = "<col=A6A6A6>";
	public static String lightgray = "<col=C4C4C4>";
	public static String darkred = "<col=B52100>";
	public static String yellow = "<col=FFFF00>";
	public static String brown = "<col=8C6C0D>";
	public static String gold = "<col=E6CC07>";
	public static String purple = "<col=BB19E3>";

	/**
	 * Converts a color name (red, blue, etc) into hex value (ff0000)
	 */
	public static String getHexByName(String name) {
		try {
			Field field = Colors.class.getDeclaredField(name.toLowerCase());
			String value = (String) field.get(null);  // <col=ff0000>
			return value.replace("<col=", "").replace(">", "");
		} catch (Exception e) {
			return null;
		}
	}

	public static String getPresetList() {
		StringBuilder sb = new StringBuilder();
		for (Field field : Colors.class.getDeclaredFields()) {
			if (field.getType() == String.class) {
				sb.append(field.getName()).append(", ");
			}
		}
		return sb.substring(0, sb.length() - 2);
	}

}
