package org.fpeterek.til.interpreter.exceptions

class UndefinedSymbol(symbol: String) : TilException("Undefined symbol '$symbol'")
