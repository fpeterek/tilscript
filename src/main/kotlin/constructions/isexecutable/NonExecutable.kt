package org.fpeterek.til.typechecking.constructions.isexecutable

interface NonExecutable : IsExecutable {
    override val isExecutable: Boolean
        get() = false
}
