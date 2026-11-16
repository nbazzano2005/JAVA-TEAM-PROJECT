package visualnovel.model;
import java.util.Map;
/** Validated story graph and its designated starting scene. */
public final class Story {
    private final String startId;
    private final Map<String,StoryNode> nodes;
    /** Creates a graph and checks references before a game can start.
     * @param startId starting scene ID
     * @param nodes all scenes, indexed by ID
     * @throws IllegalArgumentException for missing scenes or invalid destinations
     */
    public Story(String startId,Map<String,StoryNode> nodes) {
        this.startId=startId; this.nodes=Map.copyOf(nodes);
        if (startId==null || !nodes.containsKey(startId)) throw new IllegalArgumentException("Starting scene is missing.");
        for (StoryNode node:nodes.values()) {
            if (!nodes.containsKey(node.getId()) || nodes.get(node.getId()) != node)
                throw new IllegalArgumentException("Scene map key does not match its ID.");
            if (node.isExam() && (!nodes.keySet().containsAll(java.util.List.of("passedEnding","failedEnding","burnoutEnding","communityEnding","honorsEnding"))))
                throw new IllegalArgumentException("Exam endings are missing.");
            if (!node.getContinuation().isEmpty() && !nodes.containsKey(node.getContinuation()))
                throw new IllegalArgumentException("Unknown Continue destination.");
            for (Choice choice:node.getChoices()) {
                new PlayerStats().applyChanges(choice.getRequirements());
                if (!node.isExam() && !nodes.containsKey(choice.getNextNodeId()))
                    throw new IllegalArgumentException("Unknown destination: "+choice.getNextNodeId());
                if (node.isExam() && !choice.getNextNodeId().isEmpty())
                    throw new IllegalArgumentException("Exam destinations must be empty.");
                new PlayerStats().applyChanges(choice.getStatChanges());
            }
        }
    }
    /** @return immutable scene graph for documentation and validation */
    public Map<String,StoryNode> getNodes() { return nodes; }
    /** @return designated starting scene ID */
    public String getStartId() { return startId; }
    /** Looks up a scene.
     * @param id scene ID
     * @return matching scene
     * @throws IllegalArgumentException if no scene matches
     */
    public StoryNode getNode(String id) {
        StoryNode node=nodes.get(id);
        if (node==null) throw new IllegalArgumentException("Unknown scene: "+id);
        return node;
    }
}
