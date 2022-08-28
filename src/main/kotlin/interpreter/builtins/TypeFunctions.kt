package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.sentence.EmptyList
import org.fpeterek.tilscript.interpreter.types.*
import org.fpeterek.tilscript.interpreter.util.SrcPosition


object TypeFunctions {

    private val noPos get() = SrcPosition(-1, -1)

    object GetType : EagerFunction(
        "GetType",
        Types.Type,
        listOf(
            Variable("name", noPos, Types.Text),
        ),
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val name = args[0]

            if (name !is Text) {
                return Nil(ctx.position, reason="Type name must not be symbolic")
            }

            return when (val type = interpreter.getType(name.value)) {
                null -> Nil(ctx.position, reason="Type $name was not found")
                else -> TypeRef(type, ctx.position)
            }
        }

    }

    object ConsFunctionType : EagerFunction(
        "ConsFunctionType",
        Types.Type,
        listOf(
            Variable("returnType", noPos, Types.Type),
            Variable("argTypes", noPos, ListType(Types.Type)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val returnType = args[0]
            val argTypes = args[1]

            if (returnType !is TypeRef || argTypes !is TilList) {
                return Nil(ctx.position, reason = "Function type cannot be constructed from symbolic values")
            }

            val argList = argTypes.toKotlinList()

            if (argList.any { it !is TypeRef }) {
                return Nil(ctx.position, reason = "Function type cannot be constructed from symbolic values")
            }

            return TypeRef(FunctionType(returnType.type, argList.map { (it as TypeRef).type }), ctx.position)
        }
    }

    object FunctionTypeAsList : EagerFunction(
        "FunctionTypeAsList",
        ListType(Types.Type),
        listOf(
            Variable("type", noPos, Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val fn = args[0]

            if (fn !is TypeRef || fn.type !is FunctionType) {
                return Nil(ctx.position, reason = "FunctionTypeAsList expects a function type")
            }

            return (listOf(fn.type.imageType) + fn.type.argTypes)
                .foldRight(EmptyList(Types.Type, ctx.position) as TilList) { type, acc ->
                    ListCell(TypeRef(type, ctx.position), acc, Types.Type, ctx.position)
                }
        }
    }

    object FunctionTypeAt : EagerFunction(
        "FunctionTypeAt",
        Types.Type,
        listOf(
            Variable("type", noPos, Types.Type),
            Variable("idx", noPos, Types.Int),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val fn = args[0]
            val idx = args[1]

            if (fn !is TypeRef || fn.type !is FunctionType) {
                return Nil(ctx.position, reason = "FunctionTypeAt expects a function type")
            }

            if (idx !is Integral) {
                return Nil(ctx.position, reason = "Index must not be symbolic")
            }

            if (idx.value < 0 || idx.value > fn.type.argTypes.size) {
                return Nil(ctx.position, reason = "Index out of range")
            }

            val type = when (val i = idx.value.toInt()) {
                0    -> fn.type.imageType
                else -> fn.type.argTypes[i - 1]
            }

            return TypeRef(type, ctx.position)
        }
    }

    object TupleTypeAt : EagerFunction(
        "TupleTypeAt",
        Types.Type,
        listOf(
            Variable("type", noPos, Types.Type),
            Variable("idx", noPos, Types.Int),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val tupleType = args[0]
            val idx = args[1]

            if (tupleType !is TypeRef || tupleType.type !is TupleType) {
                return Nil(ctx.position, reason = "TupleTypeAt expects a tuple type")
            }

            if (idx !is Integral) {
                return Nil(ctx.position, reason = "Index must not be symbolic")
            }

            if (idx.value < 0 || idx.value >= tupleType.type.types.size) {
                return Nil(ctx.position, reason = "Index out of range")
            }

            return TypeRef(tupleType.type.types[idx.value.toInt()], ctx.position)
        }
    }

    object ConsTupleType : EagerFunction(
        "ConsTupleType",
        Types.Type,
        listOf(
            Variable("types", noPos, ListType(Types.Type)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val types = args[0]

            if (types !is TilList) {
                return Nil(ctx.position, reason="Cannot construct a tuple type from symbolic values")
            }

            val typeList = types.toKotlinList()

            if (typeList.any { it !is TypeRef }) {
                return Nil(ctx.position, reason = "Cannot construct a tuple type from symbolic values")
            }

            return TypeRef(TupleType(typeList.map { (it as TypeRef).type }), ctx.position)
        }
    }

    object ConsListType : EagerFunction(
        "ConsListType",
        Types.Type,
        listOf(
            Variable("type", noPos, Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val type = args[0]

            if (type !is TypeRef) {
                return Nil(ctx.position, reason="Cannot construct a list type from symbolic values")
            }

            return TypeRef(ListType(type.type), ctx.position)
        }
    }

    object ListValueType : EagerFunction(
        "ListValueType",
        Types.Type,
        listOf(
            Variable("type", noPos, Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val type = args[0]

            if (type !is TypeRef || type.type !is ListType) {
                return Nil(ctx.position, reason = "ListValueType expects a list type")
            }

            return TypeRef(type.type.type, ctx.position)
        }
    }

    object ConsGenericType : EagerFunction(
        "ConsGenericType",
        Types.Type,
        listOf(
            Variable("idx", noPos, Types.Int),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val num = args[0]

            if (num !is Integral) {
                return Nil(ctx.position, reason="Cannot construct a generic type from a symbolic index")
            }

            if (num.value < 0 || num.value > Int.MAX_VALUE) {
                return Nil(ctx.position, reason="Index out of range")
            }

            return TypeRef(GenericType(num.value.toInt()), ctx.position)
        }
    }

    object GenericTypeNumber : EagerFunction(
        "GenericTypeNumber",
        Types.Int,
        listOf(
            Variable("type", noPos, Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val type = args[0]

            if (type !is TypeRef || type.type !is GenericType) {
                return Nil(ctx.position, reason="GenericTypeNumber expects a generic type")
            }

            return Integral(type.type.argNumber.toLong(), ctx.position)
        }
    }
}
