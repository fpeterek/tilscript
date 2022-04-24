package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class EntityDef(val names: List<EntityName>, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos)
