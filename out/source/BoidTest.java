import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ea.Individual; 
import ea.Selector; 
import implementations.ProportionalSelector; 
import implementations.BoidPopulation; 
import implementations.BoidIndividual; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BoidTest extends PApplet {

class Boid  {
    protected PVector pos; 
    protected PVector vel; 
    protected PVector acc; 
    protected boolean isAlive;
    protected float maxForce; //max steering force
    protected float maxSpeed; 
    protected final float SIZE;
    protected final float SEP_WEIGHT;
    protected final float ALI_WEIGHT;
    protected final float COH_WEIGHT;
    protected final float INS_WEIGHT;
    
    public Boid(float x, float y, float SEP_WEIGHT, float ALI_WEIGHT, float COH_WEIGHT, float INS_WEIGHT) {
        //hard-coded traits
        this.SIZE = 3;
        this.maxForce = 0.1f;
        this.maxSpeed = 3;

        //param-defined traits
        this.SEP_WEIGHT = SEP_WEIGHT;
        this.ALI_WEIGHT = ALI_WEIGHT;
        this.COH_WEIGHT = COH_WEIGHT;
        this.INS_WEIGHT = INS_WEIGHT;
        
        //initialization
        isAlive = true;
        acc = new PVector(0,0);
        float angle = random(TWO_PI);
        vel = new PVector(cos(angle), sin(angle));
        pos = new PVector(x,y);
    } //Constructor

    public void flock (ArrayList<Boid> curr, ArrayList <Boid> other) {
      //left for individual boid implementation
    } //flock

    public void run (ArrayList <Boid> curr, ArrayList <Boid> other) {
      flock(curr, other);  //implemented in children
      update(); //update pos, vel, acc
      render(); //draw to screen
    } //run
    
    public void applyForce(PVector force) {
        acc.add(force);
    } //applyForce 
    
    public void update() {
        //update vel, pos
        vel.add(acc);
        vel.limit(maxSpeed);
        pos.add(vel);
        
        
        //inter-wall movement
        if (pos.x >= width) 
            pos.x = 0;
        else if (pos.x <= 0)
            pos.x = width;
        if (pos.y >= height) 
            pos.y = 0;
        else if (pos.y <= 0) 
            pos.y = height;
        
        //acceleration reset
        acc.mult(0);
    } //update
    
    public void render() {
        float theta = vel.heading() + radians(90);
        
        noFill();
        
        pushMatrix();
        translate(pos.x, pos.y);
        rotate(theta);
        beginShape(TRIANGLES);
        vertex(0, -SIZE * 2);
        vertex( -SIZE, SIZE * 2);
        vertex(SIZE, SIZE * 2);
        endShape();
        popMatrix();
    } //render
    
    public PVector sep(ArrayList <Boid> boids) {
        float desiredSep = 25.0f;
        PVector steer = new PVector(0,0,0);
        int count = 0;
        
        for (Boid other : boids) {
            float d = PVector.dist(pos, other.pos);
            
            if (d > 0 && d < desiredSep) {
                PVector diff = PVector.sub(pos, other.pos);
                diff.normalize();
                diff.div(d);
                steer.add(diff);
                count++;
            } //if
        } //for
        
        if (count > 0)
            steer.div((float)count);
        
        if (steer.mag() > 0) {
            steer.normalize();
            steer.mult(maxSpeed);
            steer.sub(vel);
            steer.limit(maxForce);
        } //if
        
        return steer;
    } //sep 
    
  public PVector ali (ArrayList <Boid> boids) {
    float neighborDist = 50;
    PVector sum = new PVector (0,0);
    int count = 0;
    
    for (Boid other : boids) {
      float d = PVector.dist(pos, other.pos);
      if ((d > 0) && (d < neighborDist)) {
        sum.add(other.vel);
        count++;
      } //if
    } //for
    
    if (count > 0) {
        sum.div((float) count);
        sum.normalize();
        sum.mult(maxSpeed);
        PVector steer = PVector.sub(sum, vel);
        steer.limit(maxForce);
        return steer;
    } else {
        return new PVector (0,0);
    } //if
  } //ali

  private PVector seek (PVector target) {
    PVector desired = PVector.sub(target, pos);
    desired.normalize();
    desired.mult(maxSpeed);
    
    PVector steer = PVector.sub(desired,vel);
    steer.limit(maxForce);
    return steer;
  } //seek

  public PVector coh (ArrayList <Boid> boids) {
    float neighborDist = 50;
    PVector sum = new PVector (0,0);
    int count = 0;
    
    for (Boid other : boids) {
      float d = PVector.dist(pos, other.pos);
      
      if ((d > 0) && (d < neighborDist)) {
        sum.add(other.pos);
        count++;
      } //if
    } //for
    
    if (count > 0) {
      sum.div(count);
      return seek(sum);
    } else {
        return new PVector(0,0);
    } //if
  } //coh 
    
    
} //Boid
class Flock {
    ArrayList <Boid> boids;
    ArrayList <Boid> dead;

  
  Flock() {
    boids = new ArrayList <Boid> ();
  } //Flock
  
  public void run(ArrayList <Boid> other) {
    dead = new ArrayList <Boid> ();
    
    for (Boid b : boids) {
        if(!b.isAlive)
            dead.add(b);
        else
            b.run(boids,other);
    } //for
    if (dead != null)
      boids.removeAll(dead);
  } //run
  
  public void addBoid(Boid b) {
    boids.add(b);
  } //addBoid
  
   public ArrayList <Boid> getBoids() {
     return boids;
   } //getBoids
} //Flock
class Predator extends Boid {
    public Predator(float x, float y, float SEP_WEIGHT, float ALI_WEIGHT, float COH_WEIGHT, float INS_WEIGHT) {
        super(x, y, SEP_WEIGHT, ALI_WEIGHT, COH_WEIGHT, INS_WEIGHT);
        super.maxForce = 0.2f;
        super.maxSpeed = 7;
    } //Constructor
    
    public @Override void flock(ArrayList<Boid> curr, ArrayList <Boid> other) {
        PVector s = super.sep(curr);
        PVector a = super.ali(curr);
        PVector c = super.coh(curr);
        PVector p = predate(other);
        
        s.mult(super.SEP_WEIGHT);
        a.mult(super.ALI_WEIGHT);
        c.mult(super.COH_WEIGHT);
        p.mult(super.INS_WEIGHT);
        
        super.applyForce(s);
        super.applyForce(a);
        super.applyForce(c); 
        super.applyForce(p);  
    } //flock
    
    public @Override void render() {
        stroke(255,0,0);
        super.render();
    } //render
    
    public PVector predate(ArrayList <Boid> prey) {
        float predDist = 50;
        PVector sum = new PVector(0,0);
        int count = 0;
        
        for (Boid p : prey) {
            float d = PVector.dist(super.pos, p.pos);
            
           if ((d > 0) && (d < predDist)) {
               if (d < SIZE)
                p.isAlive = false;
                sum.add(p.pos);
                count++;
            } //if
        } //for
        
        if(count > 0) 
            sum.div(count);
        else 
            return new PVector(0,0);
        
        PVector desired = PVector.sub(sum, pos);
        desired.normalize();
        desired.mult(maxSpeed);
        
        PVector steer = PVector.sub(desired,vel);
        steer.limit(maxForce);
        return steer;
    } //predate
    
    
} //Predator
class Prey extends Boid {

    public Prey(float x, float y, float SEP_WEIGHT, float ALI_WEIGHT, float COH_WEIGHT, float INS_WEIGHT) {
        super(x, y, SEP_WEIGHT, ALI_WEIGHT, COH_WEIGHT, INS_WEIGHT);
    } //Constructor
    
    public @Override void flock(ArrayList<Boid> curr, ArrayList <Boid> other) {
        PVector s = super.sep(curr);
        PVector a = super.ali(curr);
        PVector c = super.coh(curr);
        PVector e = escape(other);
        
        s.mult(super.SEP_WEIGHT);
        a.mult(super.ALI_WEIGHT);
        c.mult(super.COH_WEIGHT);
        e.mult(super.INS_WEIGHT);
        
        applyForce(s);
        applyForce(a);
        applyForce(c); 
        applyForce(e);  
    } //flock
    
    public @Override void render() {
        stroke(255);
        super.render();
    } //render
    
  public PVector escape(ArrayList <Boid> pred) {
      float avoidDist = 200;
      PVector sum = new PVector (0,0);
      int count = 0;
      
      for (Boid p : pred) {
        float d = PVector.dist(super.pos, p.pos);
      
      if ((d >= 0) && (d < avoidDist)) {
        if (d < super.SIZE)
          isAlive = false;
          //line(p.pos.x,p.pos.y,pos.x,pos.y);
        sum.add(p.pos);
        count++;
        } //if
      } //for
      
      if (count > 0) 
        sum.div(count);
      else 
        return new PVector(0,0);
    
    PVector desired = PVector.sub(sum, pos);
    desired.normalize();
    desired.mult(maxSpeed);
    desired.x = -1 * desired.x;
    desired.y = -1 * desired.y;

    
    PVector steer = PVector.sub(desired,vel);
    steer.limit(maxForce);
    return steer;
  } //avoid
    
} //Predator






int fit;

final int FRAME_LIMIT = 500;
final int POPULATION_SIZE = 2;    
final int RUNS = 1;
final int MAX_GENERATIONS = 50;
final int NUM_TRIALS = 3;

boolean doneSetup = false;
int generationCounter = 1;
int trialCounter = 0;
int simulationCounter = 0;

Individual[] predatorBoids;
Individual[] preyBoids;
ProportionalSelector selector;
BoidPopulation predatorPopulation;
BoidPopulation preyPopulation;
Individual[] predatorResults;
Individual[] preyResults;

Sim s;
    
    public void settings() {
      size(500,500);
      //fullScreen();
    } 
    
    public void setup() {
        if (!doneSetup) {
            GA_Setup();
            // shuffle the populations
            predatorPopulation.shuffle();
            preyPopulation.shuffle();

            doneSetup = true;
        }

        if (simulationCounter == 0) {
            // create first sim
            s = new Sim(predatorPopulation.getIndividual(0).getGenome(), preyPopulation.getIndividual(0).getGenome());
        }

        if (generationCounter >= MAX_GENERATIONS) {
            exit();
        }
    } 

    public void GA_Setup() {
        predatorBoids = new Individual[POPULATION_SIZE];
        preyBoids = new Individual[POPULATION_SIZE];
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
            predatorBoids[i] = new BoidIndividual();
            preyBoids[i] = new BoidIndividual();
        }
        
        // Create selector and populations
        selector = new ProportionalSelector();
        predatorPopulation = new BoidPopulation(predatorBoids);
        preyPopulation = new BoidPopulation(preyBoids);
        

        predatorResults = new Individual[MAX_GENERATIONS];
        preyResults = new Individual[MAX_GENERATIONS];
    }
    
    public void drawHandler() {
    
    // ###### CODE FOR SIMULATION FOR LOOP ###### //
        if (frameCount < FRAME_LIMIT) {
            // performing a simulation
            s.exec();
        } else {
          if (simulationCounter < POPULATION_SIZE) {
            // simulation has ended
            // assign trial fitnesses
            fit = s.prey.getBoids().size();
            
            predatorPopulation.getIndividual(simulationCounter).setTrial(trialCounter, 100 - fit);
            preyPopulation.getIndividual(simulationCounter).setTrial(trialCounter, fit);

            // reset fit, and increment simulation counter
            fit = 0;
            simulationCounter++;

            // reeset simulation
            background(255,0,0);
            frameCount = 0;
            if (simulationCounter <= POPULATION_SIZE - 1) 
                s = new Sim(predatorPopulation.getIndividual(simulationCounter).getGenome(), preyPopulation.getIndividual(simulationCounter).getGenome());
          } else {
            // END OF TRIAL
            // reset simulation counter
            simulationCounter = 0;
            // increment trial counter
            trialCounter++;

            // shuffle populations
            predatorPopulation.shuffle();
            preyPopulation.shuffle();

            // reset sim stuff
            text("Trial: " + trialCounter + "\nGeneration: " + generationCounter, 50,50);
            background(255,0,0);
            frameCount = 0;
          }
    // ###### CODE FOR SIMULATION FOR LOOP ###### //
        
    // ###### CODE FOR GENERATION FOR LOOP ###### // 
          if (trialCounter >= NUM_TRIALS) {
            // only perform runGeneration after the set number of trials have been completed
            predatorPopulation.runGeneration(selector);
            preyPopulation.runGeneration(selector);
            
            System.out.println(predatorPopulation.getIndividual(0).printGenome());
            System.out.println(preyPopulation.getIndividual(0).printGenome());

            // reset trial counter
            trialCounter = 0;

            // increase generation counter
            generationCounter++;
          }

          setup();
    // ###### CODE FOR GENERATION FOR LOOP ###### // 

        }
    } //drawHandler

    public void draw() {
        //System.out.println("HERE");
        drawHandler();
    }


/*
public class Runner {
    public static void main(String[] args) {
        // create instance of BoidEvolution
        BoidEvolution boidEvolution = new BoidEvolution();
        boidEvolution.run();
    }
}

*/
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
            pred.addBoid(new Predator(0.25f * width, 0.25f * height, (float) preyWeights[0], (float) preyWeights[1], (float) preyWeights[2], (float) preyWeights[3]));
        } //for
    }
    
    public void exec() {
        background(0);
        prey.run(pred.getBoids()); //runs prey
        pred.run(prey.getBoids()); //runs pred
    } // exec
                
    public void printStats(){
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
                
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BoidTest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
