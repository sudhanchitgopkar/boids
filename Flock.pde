class Flock {
    ArrayList <Boid> boids;
    ArrayList <Boid> dead;

  
  Flock() {
    boids = new ArrayList <Boid> ();
  } //Flock
  
  void run(ArrayList <Boid> other) {
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
  
  void addBoid(Boid b) {
    boids.add(b);
  } //addBoid
  
   ArrayList <Boid> getBoids() {
     return boids;
   } //getBoids
} //Flock
