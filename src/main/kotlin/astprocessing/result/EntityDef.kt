package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

class EntityDef(val names: List<EntityName>, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos)
