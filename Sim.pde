Flock prey;
Flock pred;

final int NUM_PREY = 100;
final int NUM_PRED = 10;
final int NUM_FRAMES = 500;

void settings() {
    //size(500,500);
    fullScreen();
} //settings

void setup() {
    frameRate(10000); //max fR
    prey = new Flock();
    pred = new Flock();
    
    for (int i = 0; i < NUM_PREY; i++) {
        prey.addBoid(new Prey(width / 2, height / 2, 2,1,1,2));
    } //for
    
    for (int i = 0; i < NUM_PRED; i++) {
        pred.addBoid(new Predator(0.25 * width, 0.25 * height,2,1,1,2));
    } //for
} //setup

void exec() {
    if (frameCount < NUM_FRAMES) {
        background(0);
        prey.run(pred.getBoids()); //runs prey
        pred.run(prey.getBoids()); //runs pred
    } else {
        calcStats(); 
        background(255,0,0); //visually indicate reset
        frameCount = 0; //reset frameconut
        setup(); //reset flocks
    }
} // draw

void draw() {
    exec();
}

void exit() {
    calcStats();
}

void calcStats() {
    System.out.println("==== RUN STATS ====");
    System.out.println("Fitness: N/A");
    System.out.println("Initial Pred: " + NUM_PRED
        + "\nInitial Prey: " + NUM_PREY
        + "\nFrames Run: " + NUM_FRAMES);
    System.out.println("Prey Eaten: " + (NUM_PREY - prey.getBoids().size()));
    System.out.println("Avg Prey Eaten/Frame: " 
        + (float)(NUM_PREY - prey.getBoids().size()) / NUM_FRAMES);
} //calcStats
