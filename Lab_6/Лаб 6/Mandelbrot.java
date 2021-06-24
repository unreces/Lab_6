import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator{
	public final int MAX_ITERATIONS = 2000;

	public void getInitialRange(Rectangle2D.Double range){
		range.x = -2;
		range.y = -1.5;

		range.width = 3;
		range.height = 3;

	}

	public int numIterations(double x, double y){
		ComplexNumber c = new ComplexNumber();

		int counter = 0;
		while (c.getSquare() < 4 && counter < MAX_ITERATIONS){
			c.manUpdate(x, y);
			counter++;
		}

		return counter >= MAX_ITERATIONS ? -1 : counter;
	}

	public String toString(){
		return "Mandelbrot";
	}
}