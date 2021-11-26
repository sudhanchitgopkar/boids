class Flock {
    ArrayList <Boid> boids;
  
  Flock() {
    boids = new ArrayList <Boid> ();
  } //Flock
  
  void run(ArrayList <Boid> other) {
    for (Boid b : boids) {
      b.run(other, boids);
    } //for
  } //run
  
  void addBoid(Boid b) {
    boids.add(b);
  } //addBoid
  
   ArrayList <Boid> getBoids() {
     return boids;
   } //getBoids
} //Flock
