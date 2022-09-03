package org.fpeterek.tilscript.common.sentence.isexecutable

interface Executable : IsExecutable {
    override val isExecutable: Boolean
        get() = true
}
