# Suggested 5–8 minute demonstration

| Time | What to demonstrate |
|---|---|
| 0:00–0:40 | Introduce the game: a student balances preparation, teamwork, and energy before a final. Show New Story. |
| 0:40–2:20 | Play Monday’s three choices. Point out stat changes. At the whiteboard, explain the locked/unlocked support requirement. |
| 2:20–3:00 | Save a slot, make a choice, then Load to restore it. Briefly show the decision Journal. |
| 3:00–5:30 | Continue Tuesday and Wednesday, summarize the dialogue, and reach an ending. |
| 5:30–6:30 | Explain how choices produced the result. Show the ending collection or another saved ending if available. |
| 6:30–8:00 | If time allows, explain MVC and answer questions. |

This is a rehearsal plan, not measured presentation timing. A run has around 600 words. Summarize dialogue naturally instead of reading every button and response aloud. Keep gradual text reveal off during the demonstration.

## Reliable passing demonstration

Choose, in order: **1, 1, 1, 1, 1, 1, 2, 1**. Use Continue between scenes and Open Your Results at the end. This produces the Forward passing ending. To show a conditional option, note that the whiteboard extension is locked on this route because Support is below 3; the regular explanation remains available.

For a route that unlocks the whiteboard challenge, choose **2, 2** first. The extension option then becomes available at decision three. STORY_GUIDE.md lists complete routes for all endings.

## Architecture explanation

A choice button sends its index to GameController. StoryEngine checks the requirement, applies stat changes, records history, and selects the next scene. GameController requests autosave and refreshes the view. SaveManager stores an immutable GameState. StoryLoader reads the external story, while ScenePanel draws the graphics.

Before class, run the demonstration once in your own NetBeans installation. Verify save location, font readability, keyboard controls, and the correct project folder. Keep the older project separate.
