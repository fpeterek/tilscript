package org.fpeterek.til.typechecking.exceptions

class UndefinedType(typeName: String) : TilException("Undefined type '$typeName'")
