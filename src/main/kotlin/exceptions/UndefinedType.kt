package org.fpeterek.til.interpreter.exceptions

class UndefinedType(typeName: String) : TilException("Undefined type '$typeName'")
