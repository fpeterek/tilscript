package org.fpeterek.til.interpreter.exceptions

class RedefinitionOfSymbol(symbol: String) : TilException("Redefinition of symbol $symbol")
