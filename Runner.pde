    double [] predWeights = {100.0,1.0,1.0,0.0};
    double [] preyWeights = {2.0,1.0,1.0,2.0};
    
    int frameLimit;
    int fit;
    
    Sim s = new Sim(predWeights, preyWeights);
    
    public void settings() {
      size(500,500);
    } 
    
    void setup() {
      frameLimit = 500;
      fit = 0;
    } 
    
    void drawHandler() {
        if (frameCount < frameLimit) {
            s.exec();
        } else {
          fit = s.prey.getBoids().size();
          System.out.println(fit);
          s = new Sim(predWeights, preyWeights);
          background(255,0,0);
          frameCount = 0;
        }
    } //drawHandler

    void draw() {
        drawHandler();
    }
