#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes dist
find src test -name '*.java' > build/sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -d build/classes @build/sources.txt
mkdir -p build/classes/visualnovel/text
cp src/visualnovel/text/story.txt build/classes/visualnovel/text/
java -cp build/classes visualnovel.GameLogicTest
jar --create --file dist/VisualNovel.jar --main-class visualnovel.app.VisualNovelApp -C build/classes visualnovel
