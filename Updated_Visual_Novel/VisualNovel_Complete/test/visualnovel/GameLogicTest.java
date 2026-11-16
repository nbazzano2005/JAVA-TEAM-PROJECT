package visualnovel;

import visualnovel.model.*;
import visualnovel.text.StoryLoader;
import visualnovel.persistence.SaveManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Reproducible route simulation, graph coverage, boundary tests, and persistence regression. */
public final class GameLogicTest {
    private static int checks;
    private GameLogicTest() { }
    private static void check(boolean valid,String message){checks++;if(!valid)throw new AssertionError(message);}
    private static Story story() throws IOException{return new StoryLoader().load(GameLogicTest.class.getResourceAsStream("/visualnovel/text/story.txt"));}
    private static String expected(Map<String,Integer> s){
        if(s.get("knowledge")<8 || s.get("confidence")<4)return "failedEnding";
        if(s.get("energy")<1)return "burnoutEnding";
        if(s.get("social")>=5)return "communityEnding";
        if(s.get("knowledge")>=14 && s.get("confidence")>=8 && s.get("energy")>=3)return "honorsEnding";
        return "passedEnding";
    }
    private static void invalid(String input) throws Exception{
        try{new StoryLoader().load(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));throw new AssertionError("Invalid story accepted");}catch(IOException expected){checks++;}
    }
    private static void equalState(GameState a,GameState b){
        check(a.getCurrentNodeId().equals(b.getCurrentNodeId()) && a.getStatsSnapshot().equals(b.getStatsSnapshot()) && a.getHistory().equals(b.getHistory()),"complete state equality");
    }
    private static void acyclic(Story story,String id,Set<String> active,Set<String> done){
        check(!active.contains(id),"no story cycle");if(done.contains(id))return;active.add(id);
        StoryNode node=story.getNode(id);
        if(!node.getContinuation().isEmpty())acyclic(story,node.getContinuation(),active,done);
        for(Choice choice:node.getChoices())if(!node.isExam())acyclic(story,choice.getNextNodeId(),active,done);
        active.remove(id);done.add(id);
    }
    /** Runs all automated checks without opening a window. */
    public static void main(String[] args) throws Exception{
        Story story=story();check(story.getNodes().size()==41,"expanded scene count");
        acyclic(story,story.getStartId(),new HashSet<>(),new HashSet<>());
        StoryEngine engine=new StoryEngine(story);check(engine.snapshot().getStatsSnapshot().get("energy")==4,"rested baseline");
        check(story.getNode("passedEnding").getNarrative().contains("\n"),"paragraph decoding");
        check(engine.getCurrentNode().getSpeaker().equals("Narrator"),"speaker metadata");
        GameState untouched=engine.snapshot();
        try{engine.choose(0);throw new AssertionError("Narrative choice accepted");}catch(IndexOutOfBoundsException expected){equalState(untouched,engine.snapshot());}
        engine.advance();check(engine.getCurrentNode().getId().equals("d0"),"Continue progression");
        try{engine.advance();throw new AssertionError("Invalid continuation accepted");}catch(IllegalStateException expected){checks++;}
        engine.choose(0);check(engine.snapshot().getStatsSnapshot().get("knowledge")==2,"exact stat delta");
        check(engine.snapshot().getHistory().size()==2,"history includes Continue and choice");
        engine.startNewGame();check(engine.snapshot().getHistory().isEmpty(),"reset clears history");
        try{story.getNode("d0").getChoices().clear();throw new AssertionError("Choices mutable");}catch(UnsupportedOperationException expected){checks++;}
        engine.restore(new GameState("d2",Map.of("knowledge",8,"social",2,"energy",4,"confidence",5)));
        check(!engine.canChoose(1),"conditional option locked");GameState before=engine.snapshot();
        try{engine.choose(1);throw new AssertionError("Locked choice accepted");}catch(IllegalArgumentException expected){equalState(before,engine.snapshot());}
        engine.restore(new GameState("d2",Map.of("knowledge",8,"social",3,"energy",4,"confidence",5)));check(engine.canChoose(1),"conditional option unlock boundary");
        // Exact boundary cases verify ending precedence independently of random runs.
        int[][] cases={{7,8,4,8},{8,3,4,8},{8,4,0,0},{8,4,1,0},{14,8,3,0},{14,8,3,5},{13,8,3,0},{14,7,3,0},{14,8,2,0}};
        for(int[] a:cases){var stats=Map.of("knowledge",a[0],"confidence",a[1],"energy",a[2],"social",a[3]);engine.restore(new GameState("results",stats));engine.choose(0);check(engine.getCurrentNode().getId().equals(expected(stats)),"exact ending boundary");}
        Path folder=Files.createTempDirectory("finals-week-tests-");
        Set<String> scenes=new HashSet<>(),edges=new HashSet<>();Map<String,Integer> outcomes=new TreeMap<>();
        Set<Integer> lengths=new TreeSet<>();Random random=new Random(3212026L);
        try{
            SaveManager manager=new SaveManager(folder.resolve("slot.properties"));check(manager.load().isEmpty(),"empty slot");
            for(int run=0;run<2000;run++){
                engine.startNewGame();int steps=0,decisions=0;
                while(true){
                    check(++steps<70,"bounded completion");StoryNode node=engine.getCurrentNode();
                    if(scenes.add(node.getId())){
                        manager.save(engine.snapshot());equalState(engine.snapshot(),manager.load().orElseThrow());
                        check(!manager.getTimestamp().equals("Empty slot"),"timestamp label");
                    }
                    if(node.isEnding()){
                        check(node.getId().equals(expected(engine.snapshot().getStatsSnapshot())),"ending matches rules");
                        check(decisions==9,"8 story decisions and one results action");
                        outcomes.merge(node.getId(),1,Integer::sum);lengths.add(steps);break;
                    }
                    if(!node.getContinuation().isEmpty()){engine.advance();continue;}
                    List<Integer> available=new ArrayList<>();
                    for(int i=0;i<node.getChoices().size();i++)if(engine.canChoose(i))available.add(i);
                    check(!available.isEmpty(),"no reachable choice deadlock");
                    int selected=available.get(random.nextInt(available.size()));edges.add(node.getId()+":"+selected);decisions++;
                    GameState prior=engine.snapshot();engine.choose(selected);
                    var delta=node.getChoices().get(selected).getStatChanges();
                    for(String key:prior.getStatsSnapshot().keySet())check(engine.snapshot().getStatsSnapshot().get(key)==prior.getStatsSnapshot().get(key)+delta.getOrDefault(key,0),"stat change applied exactly once");
                    // Continue after restore must match uninterrupted progression.
                    if(run<100){GameState next=engine.snapshot();engine.restore(prior);engine.choose(selected);equalState(next,engine.snapshot());}
                }
            }
            check(scenes.equals(story.getNodes().keySet()),"every scene reached by a legal playthrough");
            check(edges.size()==25,"every option selected legally");
            check(outcomes.size()==5,"all five endings reachable from a new game");
            manager.save(engine.snapshot());GameState saved=manager.load().orElseThrow();engine.startNewGame();engine.restore(saved);equalState(saved,engine.snapshot());
            before=engine.snapshot();
            try{engine.restore(new GameState("missing",before.getStatsSnapshot()));throw new AssertionError("Missing scene accepted");}catch(IllegalArgumentException expected){equalState(before,engine.snapshot());}
            try{engine.restore(new GameState("prologue",Map.of("energy",1)));throw new AssertionError("Missing stats accepted");}catch(IllegalArgumentException expected){equalState(before,engine.snapshot());}
            for(String bad:new String[]{"currentNodeId=prologue\nknowledge=oops", "currentNodeId=prologue\nknowledge=0\nsocial=0\nenergy=6\nconfidence=0\n", "formatVersion=3\ncampaign=finals-week-presentation-1\ncurrentNodeId=prologue\nknowledge=0\nsocial=0\nenergy=6\nconfidence=0\nsavedAt=bad"}){
                Files.writeString(folder.resolve("slot.properties"),bad);try{manager.load();throw new AssertionError("Bad save accepted");}catch(IOException expected){checks++;}
            }
            Path blocked=folder.resolve("blocked");Files.createDirectory(blocked);Files.writeString(blocked.resolve("child"),"x");
            try{new SaveManager(blocked).save(saved);throw new AssertionError("Failed write ignored");}catch(IOException expected){checks++;}
        }finally{try(var paths=Files.walk(folder)){for(Path path:paths.sorted(Comparator.reverseOrder()).toList())Files.delete(path);}}
        invalid("START|x\nNODE|x|T|N|continue:missing\n");
        invalid("START|x\nNODE|x|T|N|normal\nCHOICE|Go|x||wrong=2\n");
        invalid("START|x\nNODE|x|T|N|normal\nCHOICE|Go|x||social=no\n");
        invalid("START|x\nNODE|x|T|N|normal\nNODE|x|T|N|normal\n");
        invalid("START|x\nNODE|x|T|N|exam\nCHOICE|Go||\n");
        invalid("START|x\nNODE|x|T|N|continue:x\nCHOICE|Go|x|\n");
        try{new StoryLoader().load(null);throw new AssertionError("Missing resource accepted");}catch(IOException expected){checks++;}
        System.out.println("Passed "+checks+" checks; 2,000 reproducible legal playthroughs.");
        System.out.println("Coverage: "+scenes.size()+" / 41 scenes, "+edges.size()+" / 25 choices. Playthrough scene counts: "+lengths);
        System.out.println("Ending distribution: "+outcomes);
    }
}
