package org.fpeterek.til.interpreter.exceptions

class InvalidExecutionOrder(order: Int) : TilException("Execution order must be either 1 or 2 (received $order)")
