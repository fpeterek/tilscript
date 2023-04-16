package org.fpeterek.tilscript.javamath;

import org.fpeterek.tilscript.common.interpreterinterface.FunctionInterface;
import org.fpeterek.tilscript.common.interpreterinterface.SymbolRegistrar;
import org.fpeterek.tilscript.common.sentence.Symbol;
import org.fpeterek.tilscript.common.sentence.TilFunction;
import org.fpeterek.tilscript.common.sentence.Variable;
import org.fpeterek.tilscript.common.types.StructType;
import org.fpeterek.tilscript.common.types.TypeAlias;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaMathRegistrar implements SymbolRegistrar {

  @NotNull
  @Override
  public List<TypeAlias> getAliases() {
    return new ArrayList<>();
  }

  @NotNull
  @Override
  public List<TilFunction> getFunctionDeclarations() {
    return new ArrayList<>();
  }

  @NotNull
  @Override
  public List<FunctionInterface> getFunctions() {
    return Arrays.asList(
      new InvSqrt()
    );
  }

  @NotNull
  @Override
  public List<StructType> getStructs() {
    return new ArrayList<>();
  }

  @NotNull
  @Override
  public List<Symbol> getSymbols() {
    return new ArrayList<>();
  }

  @NotNull
  @Override
  public List<Variable> getVariables() {
    return new ArrayList<>();
  }
}
