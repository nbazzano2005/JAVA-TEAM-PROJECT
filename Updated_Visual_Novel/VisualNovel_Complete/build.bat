@echo off
setlocal
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
if not exist dist mkdir dist
(for /r src %%f in (*.java) do @echo "%%f"
 for /r test %%f in (*.java) do @echo "%%f") > build\sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -d build\classes @build\sources.txt
if errorlevel 1 exit /b 1
if not exist build\classes\visualnovel\text mkdir build\classes\visualnovel\text
copy /y src\visualnovel\text\story.txt build\classes\visualnovel\text\story.txt >nul
if errorlevel 1 exit /b 1
java -cp build\classes visualnovel.GameLogicTest
if errorlevel 1 exit /b 1
jar --create --file dist\VisualNovel.jar --main-class visualnovel.app.VisualNovelApp -C build\classes visualnovel
if errorlevel 1 exit /b 1
