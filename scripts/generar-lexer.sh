#!/usr/bin/env bash
#
# Regenera src/main/java/com/mycompany/promptzal_3/lexico/Lexer.java
# a partir de src/main/jflex/promptzal.jflex.
#
# Uso:
#   ./scripts/generar-lexer.sh
#
set -euo pipefail

cd "$(dirname "$0")/.."

JFLEX_VERSION="1.9.1"
CUP_VERSION="11b-20160615-1"

JFLEX_JAR="$HOME/.m2/repository/de/jflex/jflex/$JFLEX_VERSION/jflex-$JFLEX_VERSION.jar"
CUP_JAR="$HOME/.m2/repository/com/github/vbmacher/java-cup-runtime/$CUP_VERSION/java-cup-runtime-$CUP_VERSION.jar"

if [[ ! -f "$JFLEX_JAR" ]]; then
    echo "Descargando JFlex $JFLEX_VERSION..."
    mvn -q dependency:get -Dartifact="de.jflex:jflex:$JFLEX_VERSION"
fi

if [[ ! -f "$CUP_JAR" ]]; then
    echo "Descargando java-cup-runtime $CUP_VERSION..."
    mvn -q dependency:get -Dartifact="com.github.vbmacher:java-cup-runtime:$CUP_VERSION"
fi

java -cp "$JFLEX_JAR:$CUP_JAR" jflex.Main \
    -d src/main/java/com/mycompany/promptzal_3/lexico \
    src/main/jflex/promptzal.jflex

# Quita las etiquetas <p> de los comentarios generados.
sed -i 's#<p>##g; s#</p>##g' src/main/java/com/mycompany/promptzal_3/lexico/Lexer.java
rm -f src/main/java/com/mycompany/promptzal_3/lexico/Lexer.java~
echo "Lexer.java regenerado correctamente."
