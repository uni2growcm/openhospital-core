package org.isf.maternity.model;

public enum PerinealIntegrity {
	INTACT("angal.maternity.perineal.intact"),
	FIRST_DEGREE("angal.maternity.perineal.first"),
	SECOND_DEGREE("angal.maternity.perineal.second"),
	THIRD_DEGREE("angal.maternity.perineal.third"),
	FOURTH_DEGREE("angal.maternity.perineal.fourth"),
	EPISIOTOMY("angal.maternity.perineal.episiotomy");

	private final String key;

	PerinealIntegrity(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
