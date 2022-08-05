package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class ListInitializer(
    val values: List<Construction>,
    srcPos: SrcPosition
) : Construction(srcPos)
