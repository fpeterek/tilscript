package org.fpeterek.tilscript.interpreter.exceptions

class InvalidExecutionOrder(order: Int) : TilException("Execution order must be either 1 or 2 (received $order)")
