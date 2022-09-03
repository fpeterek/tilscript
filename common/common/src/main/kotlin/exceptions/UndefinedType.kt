package org.fpeterek.tilscript.interpreter.exceptions

class UndefinedType(typeName: String) : TilException("Undefined type '$typeName'")
