/*
TODO:
- Predator v Prey Boids X
- predator chase prey
- prey avoid predator
- OnCollision Death
- Bin-Lattice Subdivision


*/
class Boid {
  //=== GLOBAL VARS ===//
  PVector pos; 
  PVector vel; 
  PVector acc; 
  float r; //size
  float maxForce; //max steering force
  float maxSpeed; 
  boolean predator;
  
  Boid (float x, float y, boolean isPredator) {
       acc = new PVector(0,0);
       float angle = random(TWO_PI);
       vel = new PVector (cos(angle), sin(angle));
       pos = new PVector(x,y);
       predator = isPredator;
       
       r = 2.0;
       
       if (predator) {
         maxSpeed = 3;
         maxForce = 0.3;
       } else  {
         maxSpeed = 2;
         maxForce = 0.03;
       }
  } //Boid
  
  void run (ArrayList <Boid> prey, ArrayList <Boid> pred) {
    if (predator) 
      predFlock(prey, pred);
    else 
      preyFlock(prey,pred);
    
    update();
    render();
  } //fun
  
  void applyForce (PVector force) {
      acc.add(force);
  } //applyForce
  
  void preyFlock (ArrayList<Boid> prey, ArrayList <Boid> pred) {
    PVector s = sep(prey);
    PVector a = ali(prey);
    PVector c = coh(prey);
    
    s.mult(1);
    a.mult(1);
    c.mult(1);
    
    applyForce(s);
    applyForce(a);
    applyForce(c);
  } //flock
  
  void predFlock (ArrayList <Boid> prey, ArrayList <Boid> pred) {
    PVector s = sep(pred);
    PVector a = ali(pred);
    PVector c = coh(pred);
    PVector p = predate(prey);
    
    s.mult(1.2);
    a.mult(1);
    c.mult(1);
    p.mult(5);
    
    applyForce(s);
    applyForce(a);
    applyForce(c); 
    applyForce(p);
  } //predFlock

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
    
    fill(200, 100);
    if (!predator)
      stroke(255);
     else 
       stroke(255,0,0);
       
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
  
  PVector sep (ArrayList <Boid> boids) {
    float desiredSep = 25.0f;
    PVector steer = new PVector (0,0,0);
    int count = 0;
    
    for (Boid other: boids) {
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
  
  PVector ali (ArrayList <Boid> boids) {
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
  
  PVector coh (ArrayList <Boid> boids) {
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
  
  PVector predate(ArrayList <Boid> prey) {
      float predDist = 5;
      PVector sum = new PVector (0,0);
      int count = 0;
      
      for (Boid p : prey) {
        float d = PVector.dist(pos, p.pos);
      
      if ((d > 0) && (d < predDist)) {
          System.out.println("HERE");
          line(p.pos.x,p.pos.y,pos.x,pos.y);
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
    
    PVector steer = PVector.sub(desired,vel);
    steer.limit(maxForce);
    return steer;
  } //predate
 
} //boid
