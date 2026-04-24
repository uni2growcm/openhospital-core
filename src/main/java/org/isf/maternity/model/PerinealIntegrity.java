package org.isf.maternity.model;

public enum PerinealIntegrity {
	INTACT("maternity.perineal.intact"),
	FIRST_DEGREE("maternity.perineal.first"),
	SECOND_DEGREE("maternity.perineal.second"),
	THIRD_DEGREE("maternity.perineal.third"),
	FOURTH_DEGREE("maternity.perineal.fourth"),
	EPISIOTOMY("maternity.perineal.episiotomy");

	private final String key;

	PerinealIntegrity(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
