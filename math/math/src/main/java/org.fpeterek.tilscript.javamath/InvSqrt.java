package org.fpeterek.tilscript.javamath;

import org.fpeterek.tilscript.common.SrcPosition;
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction;
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext;
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface;
import org.fpeterek.tilscript.common.sentence.Construction;
import org.fpeterek.tilscript.common.sentence.Nil;
import org.fpeterek.tilscript.common.sentence.Real;
import org.fpeterek.tilscript.common.sentence.Variable;
import org.fpeterek.tilscript.common.types.AtomicType;
import org.fpeterek.tilscript.common.types.Primitives;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvSqrt extends DefaultFunction {

  private static AtomicType real = Primitives.INSTANCE.getReal();

  private static Variable arg = new Variable(
    "x",
    new SrcPosition(-1, -1, ""),
    real,
    new ArrayList<>(),
    null
  );

  public InvSqrt() {
    super(
      "InvSqrt",
      Primitives.INSTANCE.getReal(),
      Collections.singletonList(arg)
      );
  }

  @NotNull
  @Override
  public Construction apply(
    @NotNull InterpreterInterface interpreterInterface,
    @NotNull List<? extends Construction> args,
    @NotNull FnCallContext ctx) {

    final Construction arg = args.get(0);

    if (! (arg instanceof Real)) {
      return new Nil(ctx.getPosition(), new ArrayList<>(), "Argument of InvSqrt must not be symbolic");
    }

    final double value = ((Real) arg).getValue();

    if (value <= 0.0) {
      return new Nil(ctx.getPosition(), new ArrayList<>(), "Argument of InvSqrt must be greater than zero");
    }

    final double res = 1.0 / Math.sqrt(value);
    return new Real(res, ctx.getPosition(), new ArrayList<>());
  }

}
