package org.fpeterek.tilscript.interpreter.exceptions

class RedefinitionOfSymbol(symbol: String) : TilException("Redefinition of symbol $symbol")
