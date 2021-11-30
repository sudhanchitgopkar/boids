class Prey extends Boid {

    public Prey(float x, float y, float SEP_WEIGHT, float ALI_WEIGHT, float COH_WEIGHT, float INS_WEIGHT) {
        super(x, y, SEP_WEIGHT, ALI_WEIGHT, COH_WEIGHT, INS_WEIGHT);
    } //Constructor
    
    @Override void flock(ArrayList<Boid> curr, ArrayList <Boid> other) {
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
    
    @Override void render() {
        stroke(255);
        super.render();
    } //render
    
  PVector escape(ArrayList <Boid> pred) {
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