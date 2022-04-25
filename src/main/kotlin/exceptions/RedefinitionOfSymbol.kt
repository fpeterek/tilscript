package org.fpeterek.til.typechecking.exceptions

class RedefinitionOfSymbol(symbol: String) : TilException("Redefinition of symbol $symbol")
