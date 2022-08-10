package org.fpeterek.til.interpreter.sentence.isexecutable

interface NonExecutable : IsExecutable {
    override val isExecutable: Boolean
        get() = false
}
