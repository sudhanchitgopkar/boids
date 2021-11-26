preyFlock prey;
predFlock pred;

int numPrey = 100;
int numPred = 5;

void settings() {
   size(500,500);
  //fullScreen();
} //settings

void setup() {
  prey = new preyFlock();
  pred = new predFlock();
  
  for (int i = 0; i < numPrey; i++) {
    prey.addBoid(new preyBoid(width/3, height/3));
  } //for
  
  for (int i = 0; i < numPred; i++) {
    pred.addBoid(new predBoid((2*width)/4, (2*height)/4));
  } //for
} //setup

void draw() {
  background(0);
  prey.run(pred.getBoids());
  pred.run(prey.getBoids());
  text("Alive Prey: " + prey.getBoids().size(), 50, height/2);

} //draw
