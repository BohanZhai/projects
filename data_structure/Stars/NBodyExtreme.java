import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
public class NBodyExtreme{
	public static double readRadius(String filename){
		In in = new In(filename);
		int firstItemInFile = in.readInt();
		double secondItemInFile = in.readDouble();
		return secondItemInFile;
	}
	public static PlanetExtreme[] readPlanets(String filename){
		In in = new In(filename);
		int numberofstars = in.readInt();
		PlanetExtreme[] planets = new PlanetExtreme[numberofstars+1];
		double universeradius = in.readDouble();
		int i = 0;
		double xP;
		double yP;
		double xV;
		double yV;
		double m;
		String img;
		while (! in.isEmpty()){
			if (i>numberofstars-1){
				break;
			}
			xP = in.readDouble();
			yP = in.readDouble();
			xV = in.readDouble();
			yV = in.readDouble();
			m = in.readDouble();
			img = in.readString();
			planets[i] = new PlanetExtreme(xP, yP, xV, yV, m, img);
			i=i+1;
		}
		return planets;
	}

	public static void main(String[] args) {
		

		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];
		PlanetExtreme[] planets = readPlanets(filename);
		double universeradius = readRadius(filename);
		double time = 0;
		StdDraw.setScale(-universeradius, universeradius);
		StdDraw.enableDoubleBuffering();
		PlanetExtreme rocket = new PlanetExtreme(1.0820e+11, 0.0000e+00, 0.0000e+00, 2.0000e+4, 4.8690e+24, "squirrel.gif");
		rocket.getradius(universeradius);
		//System.out.println(rocket.radius);
		planets[planets.length-1]=rocket;
		while (time < T){
			StdDraw.clear();
			//StdDraw.KeyReleased(key);
			StdDraw.picture(0, 0, "images/starfield.jpg");
			double [] xForces = new double[planets.length];
			double [] yForces = new double[planets.length];
			for (int i=0; i < planets.length; i++){
			    xForces[i] = planets[i].calcNetForceExertedByX(planets);
			    yForces[i] = planets[i].calcNetForceExertedByY(planets);
			    planets[i].getradius(universeradius);
			}
			for (int i =0; i < planets.length; i++){

				for (int j = i ; j < planets.length; j++) {
					if(planets[i].iscoll(planets[j])){
        		        planets[i].Collision(planets[j]);
        	        }
        	        
				}
				planets[i].update(dt,xForces[i],yForces[i]);	
				planets[i].draw();
			}
			//keyboardPanel key;
			//StdDraw.keyPressed(key);
			if(StdDraw.isKeyPressed(KeyEvent.VK_W)){
				//rocket.update(dt,0.0000e+0,2.0000e+4);
				rocket.yyVel += 2.0000e+2;
				//System.out.println("press w");
			}
			else if(StdDraw.isKeyPressed(KeyEvent.VK_S)){
				//rocket.update(dt,0.0000e+0,-2.0000e+4);
				rocket.yyVel -= 2.0000e+2;
				//System.out.println("press s");
			}
			else if(StdDraw.isKeyPressed(KeyEvent.VK_A)){
				//rocket.update(dt,0.0000e+0,-2.0000e+4);
				rocket.xxVel -= 2.0000e+2;
				//System.out.println("press s");
			}
			else if(StdDraw.isKeyPressed(KeyEvent.VK_D)){
				//rocket.update(dt,0.0000e+0,-2.0000e+4);
				rocket.xxVel += 2.0000e+2;
				//System.out.println("press s");
			}
			rocket.update(dt,0.0000e+0,0.0000e+0);
			rocket.draw();
			StdDraw.show();
			StdDraw.pause(10);
			time+=dt;
		}
	}
}