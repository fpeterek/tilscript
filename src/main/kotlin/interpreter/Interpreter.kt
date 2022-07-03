package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.namechecker.NameChecker
import org.fpeterek.til.typechecking.sentence.Sentence
import org.fpeterek.til.typechecking.typechecker.TypeChecker
import org.fpeterek.til.typechecking.types.SymbolRepository

class Interpreter {

    private val nameChecker = NameChecker(SymbolRepository())
    private val typeChecker = TypeChecker

    fun interpret(sentence: Sentence) {

    }

    fun interpret(sentences: Iterable<Sentence>) {

    }

}
