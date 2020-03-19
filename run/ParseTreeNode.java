package net.zerobone.knife.parser;

import java.lang.Object;
import java.lang.System;
import java.util.ArrayList;

final class ParseTreeNode {
	final int nonTerminal;

	final boolean isParent = false;

	Object payload = null;

	ArrayList<Object> children = new ArrayList<>();

	ParseTreeNode(int nonTerminal) {
		this.nonTerminal = nonTerminal;
	}

	void reduce() {
		System.out.println("optimizing...");
	}
}
