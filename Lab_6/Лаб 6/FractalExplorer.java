import java.awt.BorderLayout;
import java.awt.geom.Rectangle2D;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import javax.imageio.ImageIO;

public class FractalExplorer{
	// Window size
	private int winSize;
	// used to disp;ay updaating
	private JImageDisplay display;
	// for future features
	private FractalGenerator generator;
	// for complex range
	private Rectangle2D.Double range;

	private JLabel coordsLabel;
	private JComboBox<FractalGenerator> fractBox;
	private  JFileChooser fileChooser;
	private JButton savingingButton;

	private int rowsRemaining;
	private JFrame frame;

	public FractalExplorer(int winSize){
		this.winSize = winSize;
		this.range = new Rectangle2D.Double();
		this.generator = new Mandelbrot();
		this.generator.getInitialRange(this.range);
		this.display = new JImageDisplay(winSize, winSize);
		this.fileChooser = new JFileChooser();
	}

	public void createAndShowGUI(){
		// JFrame init
		
		frame = new JFrame("Pathfinder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = frame.getContentPane();

		JPanel panel1 = new JPanel();

		contentPane.setLayout(new BorderLayout());

		coordsLabel = new JLabel("Coordinates: ");
		coordsLabel.setPreferredSize(new Dimension(200,30));

		panel1.add(coordsLabel);

		ActionListener aListener = new AListener();

		fractBox = new JComboBox<FractalGenerator>();
		fractBox.addItem(new Mandelbrot());
		fractBox.addItem(new Tricorn());
		fractBox.addItem(new BurningShip());

		fractBox.addActionListener(aListener);
		fractBox.setActionCommand("Boxing");

		fractBox.setPreferredSize(new Dimension(200,30));

		panel1.add(fractBox);

		contentPane.add(panel1, BorderLayout.NORTH);

		MouseAdapter mListener = new MListener();

		display.setLayout(new BorderLayout());
		display.addMouseListener(mListener);

		contentPane.add(display, BorderLayout.CENTER);

		JPanel panel2 = new JPanel();

		JButton droppingButton = new JButton("Drop image");
		droppingButton.addActionListener(aListener);
		droppingButton.setActionCommand("Drop");
		
		// contentPane.add(droppingButton, BorderLayout.SOUTH);
		panel2.add(droppingButton);

		savingingButton = new JButton("Save image");
		savingingButton.addActionListener(aListener);
		savingingButton.setActionCommand("Save");
		
		// contentPane.add(savingingButton, BorderLayout.SOUTH);
		panel2.add(savingingButton);

		contentPane.add(panel2, BorderLayout.SOUTH);

		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}

	private void drawFractal(){
		double yCoord;
		float hue;
		int iters, rgbColor;
		FractalWorker worker;

		rowsRemaining = winSize;
		enableGUI(false);

		for (int y = 0; y < winSize; y++){
			worker = new FractalWorker(y);
			worker.execute();
		}
	}

	public void enableGUI(boolean val){
		frame.setEnabled(val);
	}

	public static void main(String[] args) {
		FractalExplorer exp = new FractalExplorer(800);
		exp.createAndShowGUI();
		exp.drawFractal();
	} 

	public class AListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();

			if (command == "Drop" || command == "Boxing"){
				generator = (FractalGenerator)fractBox.getSelectedItem();

				display.clearImage();
				generator.getInitialRange(range);
				drawFractal();
			}
			else {
				System.out.println("Saving");
				fileChooser.setDialogTitle("Выбор директории");
				FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
				fileChooser.setFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(false);

				int result = fileChooser.showSaveDialog(display);
				if (result == JFileChooser.APPROVE_OPTION ){
					try{
						ImageIO.write(display.getIm(), "png", fileChooser.getSelectedFile());
					} catch(Exception err){
						JOptionPane.showMessageDialog(display, err, "ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	public class MListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if (rowsRemaining == 0){
				double x = e.getPoint().getX();
				double y = e.getPoint().getY();

				coordsLabel.setText("Coordinates:  x -> " + x + "   y - >" + y);

				x = generator.getCoord(range.x, range.x + range.width, winSize, (int)x);
				y = generator.getCoord(range.y, range.y + range.height, winSize, (int)y);

				generator.recenterAndZoomRange(range, x, y, 0.5);
				drawFractal();
				display.repaint();
			}
		}
	}

	private class FractalWorker extends SwingWorker<Object, Object>{
		private int y;
		private int[] x_arr;

		public FractalWorker(int y){
			this.y = y;
		}

		public Object doInBackground(){
			x_arr = new int[winSize];

			double xCoord, yCoord;
			float hue;
			int iters, rgbColor;


			for (int x = 0; x < winSize; x++){
				xCoord = generator.getCoord(range.x, range.x + range.width, winSize, x);
				yCoord = generator.getCoord(range.y, range.y + range.height, winSize, y);

				iters = generator.numIterations(xCoord, yCoord);

				hue = 0.7f + (float)iters / 200f;
				rgbColor = iters == -1 ? 0 : Color.HSBtoRGB(hue, 1f, 1f);

				x_arr[x] = rgbColor;
			}

			return null;
		}

		public void done(){
			for (int x = 0; x < winSize; x++){
				display.drawPixel(x, y, x_arr[x]);
			}

			display.repaint(0, 0, y, winSize, 1);
			rowsRemaining--;

			if (rowsRemaining == 0)
				enableGUI(true);
		}
	}
}
