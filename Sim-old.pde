public class Sim {
    Flock prey;
    Flock pred;
    
    final int NUM_PREY = 100;
    final int NUM_PRED = 10;
    final int NUM_FRAMES = 1000;    
    
    public Sim(double[] predWeights, double[] preyWeights) {
        //frameRate(80); //max fR
        prey = new Flock();
        pred = new Flock();
        
        for (int i = 0; i < NUM_PREY; i++) {
            prey.addBoid(new Prey(width / 2, height / 2, (float) predWeights[0],  (float) predWeights[1], (float) predWeights[2], (float) predWeights[3]));
        } //for
        
        for (int i = 0; i < NUM_PRED; i++) {
            pred.addBoid(new Predator(0.25 * width, 0.25 * height, (float) preyWeights[0], (float) preyWeights[1], (float) preyWeights[2], (float) preyWeights[3]));
        } //for
    }
    
    public void exec() {
        background(0);
        prey.run(pred.getBoids()); //runs prey
        pred.run(prey.getBoids()); //runs pred
    } // exec
                
    void printStats(){
        System.out.println("==== RUN STATS ====");
        System.out.println("Fitness: N/A");
        System.out.println("Initial Pred: " + NUM_PRED
        + "\nInitial Prey: " + NUM_PREY
        + "\nFrames Run: " + NUM_FRAMES);
        System.out.println("Prey Eaten: " + (NUM_PREY - prey.getBoids().size()));
        System.out.println("Avg Prey Eaten/Frame: " 
        + (float)(NUM_PREY - prey.getBoids().size()) / NUM_FRAMES);
    } //calcStats
} //class
                
