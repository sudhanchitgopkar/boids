/*
TODO:
- Predator v Prey Boids X
- predator chase prey X
- prey avoid predator
- OnCollision Death
- Bin-Lattice Subdivision


*/
class preyBoid {
  //=== GLOBAL VARS ===//
  PVector pos; 
  PVector vel; 
  PVector acc; 
  float r; //size
  float maxForce; //max steering force
  float maxSpeed; 
  boolean isAlive;
  
  preyBoid (float x, float y) {
       acc = new PVector(0,0);
       float angle = random(TWO_PI);
       vel = new PVector (cos(angle), sin(angle));
       pos = new PVector(x,y);
       
       isAlive = true;
       r = 2.0;

       maxSpeed = 2;
       maxForce = 0.03;
  } //Boid
  
  void run (ArrayList <preyBoid> prey, ArrayList <predBoid> pred) {
    flock(prey,pred); 
    update();
    render();
  } //fun
  
  void applyForce (PVector force) {
      acc.add(force);
  } //applyForce
  
  void flock (ArrayList<preyBoid> prey, ArrayList <predBoid> pred) {
    PVector s = sep(prey);
    PVector a = ali(prey);
    PVector c = coh(prey);
    PVector av = avoid(pred);
    
    s.mult(1);
    a.mult(1);
    c.mult(1);
    av.mult(2);
    
    applyForce(s);
    applyForce(a);
    applyForce(c);
    applyForce(av);
  } //flock

  void update() {
    vel.add(acc);
    vel.limit(maxSpeed);
    pos.add(vel);
    
    if (pos.x >= width) 
      pos.x = 0;
    else if (pos.x <= 0)
      pos.x = width;
    if (pos.y >= height) 
      pos.y = 0;
    else if (pos.y <= 0) 
      pos.y = height;
    
    acc.mult(0);
  } //update
  
  PVector seek (PVector target) {
    PVector desired = PVector.sub(target, pos);
    desired.normalize();
    desired.mult(maxSpeed);
    
    PVector steer = PVector.sub(desired,vel);
    steer.limit(maxForce);
    return steer;
  } //seek
  
  void render() {
    float theta = vel.heading() + radians(90);
    stroke(255);
       
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(theta);
    beginShape(TRIANGLES);
    vertex(0, -r*2);
    vertex(-r, r*2);
    vertex(r, r*2);
    endShape();
    popMatrix();
  } //render
  
  PVector sep (ArrayList <preyBoid> boids) {
    float desiredSep = 25.0f;
    PVector steer = new PVector (0,0,0);
    int count = 0;
    
    for (preyBoid other: boids) {
        float d = PVector.dist(pos, other.pos);
        
        if (d > 0 && d < desiredSep) {
          PVector diff = PVector.sub (pos, other.pos);
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
  
  PVector ali (ArrayList <preyBoid> boids) {
    float neighborDist = 50;
    PVector sum = new PVector (0,0);
    int count = 0;
    
    for (preyBoid other : boids) {
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
  
  PVector coh (ArrayList <preyBoid> boids) {
    float neighborDist = 50;
    PVector sum = new PVector (0,0);
    int count = 0;
    
    for (preyBoid other : boids) {
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
  
  PVector avoid(ArrayList <predBoid> pred) {
      float avoidDist = 200;
      PVector sum = new PVector (0,0);
      int count = 0;
      
      for (predBoid p : pred) {
        float d = PVector.dist(pos, p.pos);
      
      if ((d >= 0) && (d < avoidDist)) {
        if (d < r && d >= 0)
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
  
 
} //boid
