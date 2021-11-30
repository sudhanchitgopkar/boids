class Predator extends Boid {
    public Predator(float x, float y, float SEP_WEIGHT, float ALI_WEIGHT, float COH_WEIGHT, float INS_WEIGHT) {
        super(x, y, SEP_WEIGHT, ALI_WEIGHT, COH_WEIGHT, INS_WEIGHT);
        super.maxForce = 0.2;
        super.maxSpeed = 7;
    } //Constructor
    
    @Override void flock(ArrayList<Boid> curr, ArrayList <Boid> other) {
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
    
    @Override void render() {
        stroke(255,0,0);
        super.render();
    } //render
    
    PVector predate(ArrayList <Boid> prey) {
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