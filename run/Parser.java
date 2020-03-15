package net.zerobone.knife.parser;

import java.lang.Object;
import java.lang.System;

public final class Parser {
  public static final int T_EOF = 0;

  public static final int T_MUL = 5;

  public static final int T_RIGHT_PAREN = 2;

  public static final int T_ID = 3;

  public static final int T_LEFT_PAREN = 1;

  public static final int T_PLUS = 4;

  private static final int[] terminalCount = 6;

  private static final int[] nonTerminalCount = 5;

  private static final int[] table = {
  0,9,0,8,0,0,
  6,0,7,0,5,0,
  0,2,0,1,0,0,
  0,3,0,4,0,0,
  11,0,12,0,13,10};

  private static final int[][] actionTable = {
  {2,-1,-2},
  {2,-1,-2},
  {3,1,-3,2},
  {3,3},
  {1,4,-1,-2},
  {1},
  {1},
  {0,-4,-5},
  {0,-4,-5},
  {4,5,-4,-5},
  {4},
  {4},
  {4}};

  public void parse(int tokenId, Object token) {
    System.out.println("Hello, ZeroBone!");
  }
}
