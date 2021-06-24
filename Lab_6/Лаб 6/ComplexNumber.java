public class ComplexNumber{
	// real number part
	public double re;
	// imaginary number part
	public double im;

	public ComplexNumber(){
		this.re = 0;
		this.im = 0;
	}

	public double getSquare(){
		return this.re * this.re + this.im * this.im;
	}

	public void manUpdate(double x, double y){
		double u_re = re * re - im * im + x;
		double u_im = 2 * re * im + y;

		this.re = u_re;
		this.im = u_im;
	}

	public void triUpdate(double x, double y){
		double u_re = re * re - im * im + x;
		double u_im = -2 * re * im + y;

		this.re = u_re;
		this.im = u_im;
	}

	public void shiUpdate(double x, double y){
		double u_re = re * re - im * im + x;
		double u_im = 2 * Math.abs(re) * Math.abs(im) + y;

		this.re = u_re;
		this.im = u_im;
	}
}