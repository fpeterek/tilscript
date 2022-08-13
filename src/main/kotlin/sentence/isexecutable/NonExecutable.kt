package org.fpeterek.tilscript.interpreter.sentence.isexecutable

interface NonExecutable : IsExecutable {
    override val isExecutable: Boolean
        get() = false
}
