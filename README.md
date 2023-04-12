# Tilscript

### Run

## tilscript.sh

```sh
./bin/tilscript.sh examples/skript.tils
```

## Manually

Load .jar libraries

```sh
java -cp build/libs/tilscript.jar:../examples/libs/math.jar org.fpeterek.tilscript.interpreter.MainKt ../examples/math.tils
```

Or, if you do not need any extra libraries, simply

```sh
java -jar build/libs/tilscript.jar ../examples/skript.tils
```

