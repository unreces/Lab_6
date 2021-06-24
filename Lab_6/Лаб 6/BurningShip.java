import java.awt.geom.Rectangle2D;

public class BurningShip extends FractalGenerator{
	public final int MAX_ITERATIONS = 2000;

	public void getInitialRange(Rectangle2D.Double range){
		range.x = -2;
		range.y = -2.5;

		range.width = 4;
		range.height = 4;

	}

	public int numIterations(double x, double y){
		ComplexNumber c = new ComplexNumber();

		int counter = 0;
		while (c.getSquare() < 4 && counter < MAX_ITERATIONS){
			c.shiUpdate(x, y);
			counter++;
		}

		return counter >= MAX_ITERATIONS ? -1 : counter;
	}

	public String toString(){
		return "Burning Ship";
	}
}