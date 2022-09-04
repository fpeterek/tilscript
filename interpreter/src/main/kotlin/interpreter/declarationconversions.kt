package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.TypeAlias

fun Symbol.toDeclaration() = LiteralDeclaration(listOf(this), position)

fun TilFunction.toDeclaration() = FunctionDeclaration(listOf(this), position)

fun TypeAlias.toDefinition() = TypeDefinition(this, SrcPosition(-1, -1))
