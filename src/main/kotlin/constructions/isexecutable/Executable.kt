package org.fpeterek.til.typechecking.constructions.isexecutable

interface Executable : IsExecutable {
    override val isExecutable: Boolean
        get() = true
}
