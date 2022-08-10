package org.fpeterek.til.interpreter.sentence.isexecutable

interface Executable : IsExecutable {
    override val isExecutable: Boolean
        get() = true
}
