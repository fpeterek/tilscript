package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class EntityDef(val names: List<EntityName>, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos)
