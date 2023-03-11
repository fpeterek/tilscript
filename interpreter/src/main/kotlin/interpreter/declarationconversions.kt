package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.StructType
import org.fpeterek.tilscript.common.types.TypeAlias
import org.fpeterek.tilscript.common.types.Util.trivialize

fun Symbol.toDeclaration() = LiteralDeclaration(listOf(this), position)

fun TilFunction.toDeclaration() = FunctionDeclaration(listOf(this), position)

fun TypeAlias.toDefinition() = TypeDefinition(this, SrcPosition(-1, -1))

fun StructType.toDefinition() = StructDefinition(this, SrcPosition(-1, -1))

fun Variable.toDefinition() = VariableDefinition(name, constructedType, value!!.trivialize(), position)

fun Variable.toDeclaration() = VariableDeclaration(listOf(this), position)
