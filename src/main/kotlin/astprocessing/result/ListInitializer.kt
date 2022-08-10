package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class ListInitializer(
    val values: List<Construction>,
    srcPos: SrcPosition
) : Construction(srcPos)
