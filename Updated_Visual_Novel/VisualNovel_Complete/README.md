# Finals Week — Presentation Edition

A compact Java 17 Swing visual novel for CS321 Team 6: Noah Bazzano, Trevor Hoang, Allorah Sanchez, and Perry Shipley.

Designed for a **5–8 minute classroom demonstration**, including brief feature explanations. One playthrough has eight decisions, 21 scene screens, and approximately 600–630 narrative words. Actual timing depends on reading and explanation speed. The full graph contains 41 scenes and five endings, because each choice has a distinct response. The longer Extended Edition story has been replaced by this shorter campaign.

## Run

1. Extract the ZIP into a new folder.
2. In NetBeans, File > Open Project > select VisualNovel_Complete (contains build.xml and nbproject).
3. Select JDK 17 or later if prompted. Clean and Build, then Run Project.
4. Select New Story. Main class: visualnovel.app.VisualNovelApp.

A prebuilt JAR is in dist/VisualNovel.jar. Run `java -jar dist/VisualNovel.jar` or double-click if Windows has an appropriate Java runtime association.

## Features retained

Three short chapters follow study preparation, a team presentation, and the final exam. Maya, Leo, and Professor Chen appear in the story. Choices change Knowledge, Support, Energy, and Confidence. Preparation unlocks stronger options. Five endings distinguish a steady pass, distinction, passing together, exhaustion, and an unsuccessful attempt.

The dark interface includes original Java2D cartoons, stat meters, chapter/speaker labels, keyboard shortcuts, a decision journal, three manual save slots, autosave, persistent reading preferences, and an ending collection. It uses no external media assets or libraries.

## Controls and persistence

Choose with buttons or keys 1–3. Space completes gradual text reveal or activates a sole Continue action. Click narrative to finish text reveal. Reading Settings changes font size and optional text reveal. Main Menu and Resume preserve the current session.

Save offers three manual slots; Load also offers autosave. New Story and every successful choice/Continue transition update autosave. Saving over a manual slot and loading over a running session require confirmation. Files live in the working directory: savegame.properties, savegame2.properties, savegame3.properties, autosave.properties. Starting a new story replaces autosave; use a manual slot to preserve a previous run.

Saves retain scene, all four absolute stats, decision history, campaign ID, version, and UTC timestamp. Slot labels display local date/time. Writes use a temporary file and atomic replacement where supported. Failed loads do not change the running game. Reading preferences and discovered endings use Java Preferences on that computer.

**Start fresh in this edition. Earlier versions use different scenes and balancing, so their saves are rejected with a clear message. Existing files are not automatically deleted.**

## Build and test

Windows: `build.bat`. Linux/macOS: `sh build.sh`. Both compile UTF-8 Java 17 source, copy story data, run logic tests, and create the JAR.

GUI rendering test: `java -Djava.awt.headless=true -cp build/classes visualnovel.GuiSmokeTest`. Add an output-directory argument to generate view previews. Tests use main methods, not JUnit. In NetBeans, use Run File on the test class.

See PRESENTATION_DEMO.md for a timed demonstration plan, STORY_GUIDE.md for exact ending rules and walkthroughs, DESIGN.md for MVC/UML/CRC, and TESTING.md for verified results and manual checks. Generated Javadocs and interface previews are under docs/.

## Story editing

Edit src/visualnovel/text/story.txt and rebuild. Records:

- START|id
- NODE|id|title|narrative|mode|location|speaker|chapter
- CHOICE|label|destination|knowledge=2,energy=-1|social=3

Mode is normal, continue:destination, or exam. A normal node without choices is an ending. Exam choice destinations are empty because the model determines the result. The last CHOICE field contains optional minimum requirements. Literal \n becomes a narrative line break. Literal pipes cannot appear inside text. The loader validates references, stat names, duplicate scenes, and malformed records.

Native desktop interaction and your NetBeans setup still require manual verification. No sound, animated characters, or timeline rewind is included. Scene art scales and changes with the story; text reveal is optional.
