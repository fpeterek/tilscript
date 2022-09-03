package org.fpeterek.tilscript.common.sentence.isexecutable

interface NonExecutable : IsExecutable {
    override val isExecutable: Boolean
        get() = false
}
