package org.fpeterek.tilscript.interpreter.sentence.isexecutable

interface Executable : IsExecutable {
    override val isExecutable: Boolean
        get() = true
}
