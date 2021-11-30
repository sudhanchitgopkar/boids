class Boid  {
    protected PVector pos; 
    protected PVector vel; 
    protected PVector acc; 
    protected boolean isAlive;
    protected float maxForce; //max steering force
    protected float maxSpeed; 
    protected final float SIZE; //size
    protected final float SEP_WEIGHT;
    protected final float ALI_WEIGHT;
    protected final float COH_WEIGHT;
    protected final float INS_WEIGHT;
    
    public Boid(float x, float y, float SEP_WEIGHT, float ALI_WEIGHT, float COH_WEIGHT, float INS_WEIGHT) {
        this.SIZE = 3;
        this.maxForce = 0.1;
        this.maxSpeed = 3;
        this.SEP_WEIGHT = SEP_WEIGHT;
        this.ALI_WEIGHT = ALI_WEIGHT;
        this.COH_WEIGHT = COH_WEIGHT;
        this.INS_WEIGHT = INS_WEIGHT;
        
        isAlive = true;
        acc = new PVector(0,0);
        float angle = random(TWO_PI);
        vel = new PVector(cos(angle), sin(angle));
        pos = new PVector(x,y);
    } //Constructor

    void flock (ArrayList<Boid> curr, ArrayList <Boid> other) {
      //left for individual boid implementation
  } //flock

    void run (ArrayList <Boid> curr, ArrayList <Boid> other) {
      flock(curr, other);  
      update();
      render();
    } //run
    
    void applyForce(PVector force) {
        acc.add(force);
    } //applyForce 
    
    void update() {
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
    
    void render() {
        float theta = vel.heading() + radians(90);
        
        fill(200, 100);
        
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
    
    PVector sep(ArrayList <Boid> boids) {
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

  private PVector seek (PVector target) {
    PVector desired = PVector.sub(target, pos);
    desired.normalize();
    desired.mult(maxSpeed);
    
    PVector steer = PVector.sub(desired,vel);
    steer.limit(maxForce);
    return steer;
  } //seek

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
    
    
} //Boid
