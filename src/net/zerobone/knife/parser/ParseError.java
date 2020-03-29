package net.zerobone.knife.parser;

import java.lang.Object;

public final class ParseError {
	public static final int ANY = -1;

	public final int expected;

	public final int got;

	public final Object token;

	ParseError(int expected, int got, Object token) {
		this.expected = expected;
		this.got = got;
		this.token = token;
	}
}
