public class Planet{
        public double xxPos;
        public double yyPos;
        public double xxVel;
        public double yyVel;
        public double mass;
        public String imgFileName;
        public Planet(double xP, double yP, double xV, double yV, double m, String img){
        	xxPos=xP;
        	yyPos=yP;
        	xxVel=xV;
        	yyVel=yV;
        	mass=m;
        	imgFileName=img;
        }
        public Planet(Planet p){
        
        	xxPos=p.xxPos;
        	yyPos=p.yyPos;
        	xxVel=p.xxVel;
        	yyVel=p.yyVel;
        	mass=p.mass;
        	imgFileName=p.imgFileName;
        }
        public double calcDistance(Planet plt){
        	double r = Math.sqrt((Math.pow((xxPos-plt.xxPos),2) + Math.pow((yyPos-plt.yyPos),2)));
        	return r;
        }
        private static final double G = 6.67*(Math.pow(10,-11));
        public double calcForceExertedBy(Planet plt){
        	double r = this.calcDistance(plt);
        	double force = G*this.mass*plt.mass/(r*r);  
        	return force;
        }
        public double calcForceExertedByX(Planet plt){
        	double netforce = this.calcForceExertedBy(plt)*((plt.xxPos - xxPos)/this.calcDistance(plt));
        	return netforce;
        }
        public double calcForceExertedByY(Planet plt){
        	double netforce = this.calcForceExertedBy(plt)*((plt.yyPos - yyPos)/this.calcDistance(plt));
        	return netforce;
        }
        public double calcNetForceExertedByX(Planet[] plts){
        	int i = 0;
        	double netforce = 0;
        	while (i<plts.length){
        		if(this.equals(plts[i])){
        			i++;
        			continue;
        		}
        		netforce += this.calcForceExertedBy(plts[i])*((plts[i].xxPos - xxPos)/this.calcDistance(plts[i]));
        		i++;
        	}
        	return netforce;
        }
        public double calcNetForceExertedByY(Planet[] plts){
        	int i = 0;
        	double netforce = 0;
        	while (i<plts.length){
        		if(this.equals(plts[i])){
        			i++;
        			continue;
        		}
        		netforce += this.calcForceExertedBy(plts[i])*((plts[i].yyPos - yyPos)/this.calcDistance(plts[i]));
        		i++;
        	}
        	return netforce;
        }

        public void update(double dt, double fx, double fy){
        	/** accelerate along x-axis*/
        	double ax = fx/this.mass;
            double ay = fy/this.mass;
        	xxVel += ax*dt;
        	yyVel += ay*dt;
        	xxPos += xxVel*dt;
        	yyPos += yyVel*dt;
        }
        public void draw(){
            StdDraw.picture(xxPos, yyPos, "images/"+imgFileName);
            //StdDraw.show();
        }
}
