Flock prey;
Flock pred;

int numPrey = 100;
int numPred = 10;

void settings() {
    //size(500,500);
    fullScreen();
} //settings

void setup() {
    prey = new Flock();
    pred = new Flock();
    
    for (int i = 0; i < numPrey; i++) {
        prey.addBoid(new Prey(width/2, height/2, 2,1,1,2));
    } //for
    
    for (int i = 0; i < numPred; i++) {
        pred.addBoid(new Predator(0.25 * width, 0.25 * height,2,1,1,2));
    } //for
} //setup

void draw() {
    /*
    if (frameCount > 1000)
        exit();
    */
    background(0);
    prey.run(pred.getBoids());
    pred.run(prey.getBoids());
} // draw

void exit() {
    System.out.println(prey.getBoids().size());
    System.exit(0);
}
