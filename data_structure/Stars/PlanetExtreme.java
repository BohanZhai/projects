import java.util.*;
import java.awt.*; 
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import javax.imageio.ImageIO;
public class PlanetExtreme {
	public double xxPos;
        public double yyPos;
        public double xxVel;
        public double yyVel;
        public double mass;
        public String imgFileName;
        public double xxmomentum;
        public double yymomentum;
        public double radius;
        public PlanetExtreme(double xP, double yP, double xV, double yV, double m, String img){
        	xxPos=xP;
        	yyPos=yP;
        	xxVel=xV;
        	yyVel=yV;
        	mass=m;
        	xxmomentum = mass * xxVel;
        	yymomentum = mass * yyVel;
        	imgFileName=img;
        }
        public PlanetExtreme(PlanetExtreme p){
        
        	xxPos=p.xxPos;
        	yyPos=p.yyPos;
        	xxVel=p.xxVel;
        	yyVel=p.yyVel;
        	mass=p.mass;
        	xxmomentum = p.xxmomentum;
        	yymomentum = p.yymomentum;
        	imgFileName=p.imgFileName;
        } 

        public void Collision (PlanetExtreme bang){
        	xxVel = (xxVel*(mass - bang.mass) + 2*bang.mass*bang.xxVel)/(mass + bang.mass);
        	yyVel = (yyVel*(mass - bang.mass) + 2*bang.mass*bang.yyVel)/(mass + bang.mass);
        	bang.xxVel = (bang.xxVel*(bang.mass - mass) + 2*mass*xxVel)/(bang.mass + mass);
        	bang.yyVel = (bang.yyVel*(bang.mass - mass) + 2*mass*yyVel)/(bang.mass + mass);
        }

        public double calcDistance(PlanetExtreme plt){
        	double r = Math.sqrt((Math.pow((xxPos-plt.xxPos),2) + Math.pow((yyPos-plt.yyPos),2)));
        	return r;
        }
        private static final double G = 6.67*(Math.pow(10,-11));
        public double calcForceExertedBy(PlanetExtreme plt){
        	double r = this.calcDistance(plt);
        	double force = G*this.mass*plt.mass/(r*r);  
        	return force;
        }
        public double calcForceExertedByX(PlanetExtreme plt){
        	double netforce = this.calcForceExertedBy(plt)*((plt.xxPos - xxPos)/this.calcDistance(plt));
        	return netforce;
        }
        public double calcForceExertedByY(PlanetExtreme plt){
        	double netforce = this.calcForceExertedBy(plt)*((plt.yyPos - yyPos)/this.calcDistance(plt));
        	return netforce;
        }
        public double calcNetForceExertedByX(PlanetExtreme[] plts){
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
        public double calcNetForceExertedByY(PlanetExtreme[] plts){
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


        public boolean iscoll(PlanetExtreme a){
        	if (this.calcDistance(a) <= (radius + a.radius)){
        		return true;
        	}
        	else{
        		return false;
        	}
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
        /**public BufferedImage createpicture(){
        	BufferedImage off_Image =
  			new BufferedImage(50, 50,
                    BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = off_Image.createGraphics();
        }*/

        public void getradius(double unr){
        	File imageFile = new File("images/"+imgFileName);
        	//BufferedImage bimg = ImageIO.read(new File("images/"+imgFileName));
        	double width = imageFile.length();
        	//System.out.println(width);
        	//int width = bimg.getWidth();
        	double rate = width/unr;
        	double radiusofstar = (rate/512)*0.5;
        	radius=radiusofstar;
        	//System.out.println(imgFileName+ " is "+radiusofstar);

        }
}