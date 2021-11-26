class preyFlock {
    ArrayList <preyBoid> boids;
    ArrayList <preyBoid> dead = new ArrayList <preyBoid>();;
  
  preyFlock() {
    boids = new ArrayList <preyBoid> ();
  } //Flock
  

  void run(ArrayList <predBoid> pred) {
    for (preyBoid p : boids) {
      if (!p.isAlive)
        dead.add(p);
      else 
        p.run(boids,pred);
    } //for
    if (dead != null)
      boids.removeAll(dead);
  } //run
  
  void addBoid(preyBoid b) {
    boids.add(b);
  } //addBoid
  
   ArrayList <preyBoid> getBoids() {
     return boids;
   } //getBoids
} //Flock
