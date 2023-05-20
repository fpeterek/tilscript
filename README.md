# TIL-Script

## Run

### tilscript.sh

```sh
./bin/tilscript.sh examples/skript.tils
```

See [Setting Up The Directory Structure For `tilscript.sh`](#setting-up-the-directory-structure-for-tilscriptsh)
for instructions to setup the program to work with `tilscript.sh` properly, if needed.

### Manually

Load .jar libraries

```sh
java -cp build/libs/tilscript.jar:../examples/libs/math.jar org.fpeterek.tilscript.interpreter.MainKt ../examples/math.tils
```

Or, if you do not need any extra libraries, simply run

```sh
java -jar build/libs/tilscript.jar ../examples/skript.tils
```

The example assumes you're located in the directory with the TIL-Script interpreter source code
and you've built the program using Gradle. If that is not the case, just change the path to the
.jar file, whatever the correct path is.

## Building From Source

In case you don't have access to a pre-built .jar, or if you're trying to make a contribution to
the project and you need to recompile the .jar files, you will need to rebuild everything from
source.

You will need Java 8 installed on your computer, and you might also need Gradle. You do not need
to install Maven, even though the project uses a local Maven repository.

First, you may want to build the `common` library, as all the other libraries depend on it.
Prefer using the Gradle wrapper when possible.

```sh
cd common
./gradlew clean build publishToMavenLocal
```

Next, you'll want to build the standard library. Follow the same steps as before, just make sure
to switch to the correct folder, or invoke the `gradlew` script with the proper path.

```sh
cd ../stdlib
./gradlew clean build publishToMavenLocal
```

Finally, you will want to build the interpreter, and, optionally, the `math` library. The `math`
library is optional, you only need to build it if you actually plan to use it. This time, we do not
need to publish the final .jar to the local repository.

```sh
cd interpreter
./gradlew clean build
```

The math library can be built analogously.

### Building On Windows

You can also build the source code on Windows, just use the `gradlew.bat` script instead of the
`gradlew` shell script and use Windows paths instead of Unix paths. The arguments to the Java
runtime and Gradle should likely remain the same.

## Setting Up The Directory Structure For `tilscript.sh`

First, you will need to build the entire project. Then, you will need to create a folder in which
you will place the `tilscript.sh` file (alternatively, you can just reuse the `bin/` folder in the
repository structure, everything under `bin/libs/` and all jars located in `bin/` will be ignored
by version control).

Copy the `tilscript.sh` file into the newly created folder (or skip this step if you're using
`bin/`). Then, create a `libs/` folder inside of your folder.

```sh
mkdir ts
cp tilscript/bin/tilscript.sh ts
mkdir ts/libs
```

Finally, you will want to move the interpreter `.jar` into your folder, and possibly any libraries
into the `libs/` folder.

The following example assumes you've built the interpreter and the math library and you're located
in the parent directory of this entire repository, as does the previous example.

```sh
# Move (or copy, if you want) the math lib
mv tilscript/math/build/libs/math.jar ts/libs/

# Move the interpreter jar
mv build/libs/tilscript.jar ts/
```

Then, just run the `tilscript.sh` file. Don't forget to specify the proper directory.

```sh
./ts/tilscript.sh myscript.tils
```

If you plan on using TIL-Script very often, you may want to add the script location to your `PATH`.
