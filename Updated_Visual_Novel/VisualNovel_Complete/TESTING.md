# Verification — Presentation Edition

Java 17 compilation with UTF-8 and all compiler warnings enabled passed without warnings. GameLogicTest passed **137,075 assertions across 2,000 reproducible legal playthroughs**, reaching **41/41 scenes, 25/25 selectable options, and 5/5 endings**. The 25 options comprise 24 story options plus Open Your Results. Every run visited 21 scenes and made eight decisions plus the results action.

Seeded outcomes: Forward 652; Together 249; Distinction 21; Burnout 303; Begin Again 775. These counts describe test sampling, not expected player behavior. Tests sample full playthroughs rather than enumerate every possible sequence.

Coverage includes exact stat application, Continue scenes, complete save restoration and history, malformed saves/story records, old campaign rejection, requirement boundaries, ending boundaries and precedence, immutable choices, reset, and rejected actions leaving state unchanged.

GuiSmokeTest rendered the actual Swing view for all 41 scenes, checked choice callbacks, and verified the locked whiteboard choice. Preview images were generated from the current interface. Javadocs are included. These checks do not replace native desktop testing.

## Reproduce

Run build.bat (Windows) or sh build.sh (Linux/macOS), then:

`java -Djava.awt.headless=true -cp build/classes visualnovel.GuiSmokeTest`

An optional final directory argument writes preview images. The GUI test renders endings and therefore marks them discovered in that machine’s Java Preferences; isolate the preferences root for testing if desired.

## Manual checks before class

- Open and run in NetBeans with JDK 17+.
- Rehearse PRESENTATION_DEMO.md and measure your actual time.
- Verify keyboard controls, text settings, resizing, and all save/load dialogs.
- Save, make a choice, reload, and verify restored stats, scene, and journal.
- Close/reopen and load autosave.
- Check that an older-edition or damaged save reports an error without changing the current game.
- Finish a game and check the ending collection, then replay.

NetBeans, the Windows build script, and native desktop interaction were not exercised here. Record your own results before reporting those checks as complete.
