package org.fpeterek.til.typechecking.sentence.isexecutable

interface NonExecutable : IsExecutable {
    override val isExecutable: Boolean
        get() = false
}
