package org.fpeterek.til.typechecking.exceptions

class UndefinedSymbol(symbol: String) : TilException("Undefined symbol '$symbol'")
