package visualnovel.model;
import java.util.List;
import java.util.Objects;
/** Immutable scene with narrative, options, and an optional exam transition. */
public final class StoryNode {
    private final String id, title, narrative;
    private final List<Choice> choices;
    private final boolean exam;
    private String continuation="";
    private String location="classroom", speaker="Narrator", chapter="Finals Week";
    /** Creates a fully described scene from external story metadata. */
    public StoryNode(String id,String title,String text,List<Choice> choices,boolean exam,String next,String location,String speaker,String chapter) {
        this(id,title,text,choices,exam); this.continuation=Objects.requireNonNull(next);
        this.location=Objects.requireNonNull(location); this.speaker=Objects.requireNonNull(speaker); this.chapter=Objects.requireNonNull(chapter);
    }
    /** @return illustration location */
    public String getLocation() { return location; }
    /** @return current speaking character */
    public String getSpeaker() { return speaker; }
    /** @return chapter heading */
    public String getChapter() { return chapter; }
    /** Creates a narrative-only scene with a Continue destination. */
    public StoryNode(String id,String title,String narrative,String next) {
        this(id,title,narrative,List.of(),false); continuation=Objects.requireNonNull(next);
    }
    /** @return Continue destination, or empty for choices/endings */
    public String getContinuation() { return continuation; }
    /** @return whether this scene ends the story */
    public boolean isEnding() { return choices.isEmpty() && continuation.isEmpty(); }
    /** Creates a scene.
     * @param id unique scene ID
     * @param title scene heading
     * @param narrative scene text
     * @param choices available options; empty for an ending
     * @param exam whether selecting an option evaluates the exam rules
     */
    public StoryNode(String id,String title,String narrative,List<Choice> choices,boolean exam) {
        this.id=Objects.requireNonNull(id); this.title=Objects.requireNonNull(title);
        this.narrative=Objects.requireNonNull(narrative);
        this.choices=List.copyOf(choices); this.exam=exam;
    }
    /** @return unique scene ID */
    public String getId() { return id; }
    /** @return scene heading */
    public String getTitle() { return title; }
    /** @return scene narrative */
    public String getNarrative() { return narrative; }
    /** @return immutable options */
    public List<Choice> getChoices() { return choices; }
    /** @return true for a scene that evaluates exam results */
    public boolean isExam() { return exam; }
}
