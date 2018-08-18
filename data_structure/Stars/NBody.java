public class NBody{
	public static double readRadius(String filename){
		In in = new In(filename);
		int firstItemInFile = in.readInt();
		double secondItemInFile = in.readDouble();
		return secondItemInFile;
	}
	public static Planet[] readPlanets(String filename){
		In in = new In(filename);
		int numberofstars = in.readInt();
		Planet[] planets = new Planet[numberofstars];
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
			planets[i] = new Planet(xP, yP, xV, yV, m, img);
			i=i+1;
		}
		return planets;
	}
	public static void main(String[] args) {
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];
		Planet[] planets = readPlanets(filename);
		double universeradius = readRadius(filename);
		double time = 0;
		StdDraw.setScale(-universeradius, universeradius);
		StdDraw.enableDoubleBuffering();
		while (time < T){
			StdDraw.clear();
			StdDraw.picture(0, 0, "images/starfield.jpg");
			double [] xForces = new double[planets.length];
			double [] yForces = new double[planets.length];
			for (int i=0; i < planets.length; i++){
			    xForces[i] = planets[i].calcNetForceExertedByX(planets);
			    yForces[i] = planets[i].calcNetForceExertedByY(planets);
			}
			for (int i =0; i < planets.length; i++){
				planets[i].update(dt,xForces[i],yForces[i]);
				planets[i].draw();
			}
			
			StdDraw.show();
			StdDraw.pause(10);
			time+=dt;
		}
		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", universeradius);
		for (int i = 0; i < planets.length; i++) {
    			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                  planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                  planets[i].yyVel, planets[i].mass, planets[i].imgFileName);   }
		
	}
}