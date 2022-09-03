package org.fpeterek.tilscript.interpreter.exceptions

class UndefinedSymbol(symbol: String) : TilException("Undefined symbol '$symbol'")
