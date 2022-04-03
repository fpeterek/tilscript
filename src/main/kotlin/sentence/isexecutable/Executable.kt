package org.fpeterek.til.typechecking.sentence.isexecutable

interface Executable : IsExecutable {
    override val isExecutable: Boolean
        get() = true
}
