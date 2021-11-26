class predFlock {
    ArrayList <predBoid> boids;
  
  predFlock() {
    boids = new ArrayList <predBoid> ();
  } //Flock
  
  void run(ArrayList <preyBoid> prey) {
    for (predBoid p : boids) {
      p.run(prey,boids);
    } //for
  } //run
  
  void addBoid(predBoid b) {
    boids.add(b);
  } //addBoid
  
   ArrayList <predBoid> getBoids() {
     return boids;
   } //getBoids
} //Flock
