package net.iubris.fractal_landscapes.ifs.applet;


import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import net.iubris.fractal_landscapes.ifs.common.parallel.IFSExecutor;
import net.iubris.fractal_landscapes.ifs.common.parallel.IFSExecutor.Printer;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class IFSApplet extends JApplet implements Runnable {

	private static final long serialVersionUID = -7315365287365450617L;

	private static final int DEFAULT_ITERATIONS = 200000;
	private int iterations = DEFAULT_ITERATIONS;
	
	private JAppletDrawer drawer;

	private JTextArea jTextArea;

	private JTextAreaPrinter printer;
	
	private Thread thread;

	private boolean start = false;
	
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	

	@Override
	public void init() {
		
		int width = 450;
		int height = 450;
		int textAreaHeight = 60;
		
		drawer = new JAppletDrawer(this, width-150, height-50-textAreaHeight);
		
		setSize(width, height);
		
		JLabel onscreen = new JLabel( new ImageIcon(drawer.getOnscreenImage()) );
		onscreen.setBounds(0, 100, width, 10);
		onscreen.setSize(width-100, 100);
//		JLabel offscreen = new JLabel( new ImageIcon(drawer.getOffscreenImage()) );
		jTextArea = new JTextArea(/*textAreaHeight, width*/);
		
		jTextArea.setBounds(0,0, width, textAreaHeight);
		
		
		Container contentPane = getContentPane();
		contentPane.add( jTextArea );
		contentPane.add(onscreen);
		
		printer = new JTextAreaPrinter(jTextArea);
		
		
		try {
			JSObject.getWindow(this);
			start = false;
		} catch (JSException e) {
			// not in real browser, maybe in appletviewer
			start = true;
		}
		
	}
	
	@Override
	public void run() {/*}
	
	public void myRun() {*/
		if (start) {
		// probability distribution for choosing each rule
      double[] dist = getDist();

      // update matrices
      double[][] cx = getCx();
      double[][] cy = getCy();

      // current value of (x, y)
      double x = 0.0; double y = 0.0;
		
      printer.println(iterations+" iterations");
		
		// sequentialLocal(iterations, dist, cx, cy, x, y, drawer, jTextAreaPrinter);
		long start = System.currentTimeMillis();
		printer.print("Sequential: ");
		IFSExecutor.sequential(iterations, dist, cx, cy, x, y, drawer, thread, printer);
		long end = System.currentTimeMillis();
		float time = (end - start) / 1000f;
		printer.println(" in: " + time + "s");

		printer.print("Relax! Restart in: ");
		drawer.clear();
		try {
			for (int i = 5; i > 0; i--) {
				printer.print(i + " ");
				Thread.sleep(1000);
//				drawer.clear();
			}
		} catch (InterruptedException e) {
			drawer.clear();
			e.printStackTrace();
		}
		printer.println("");
		

		drawer.clear();
		start = System.currentTimeMillis();
		printer.print("Parallel: ");
		IFSExecutor.parallel(iterations, dist, cx, cy, x, y, drawer, thread, printer);
//		drawer.show(10);
		end = System.currentTimeMillis();
		time = (end - start) / 1000f;
		printer.println(" in: " + time + "s");
		}
	}

	@Override
	public void start() {
		try {
			if (start) {
				if (thread == null) {
					thread = new Thread(this);
				}
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			printer.println(e.getMessage());
		}
	}
	
	public void call() {
		start = true;
		start();
	}

	@Override
	public void stop() {
		thread = null;
	}
	
	/*private void sequentialLocal(int iterations, double[] dist, double[][] cx, double[][] cy, double x, double y, Drawer drawer, Printer printer) {
		// do T iterations of the chaos game
  	 	int i;
      for (i = 0; i < iterations; i++) { 
          // pick a random rule according to the probability distribution
          int r = StdRandom.discrete(dist); 

          // do the update
          double x0 = cx[r][0]*x + cx[r][1]*y + cx[r][2]; 
          double y0 = cy[r][0]*x + cy[r][1]*y + cy[r][2]; 
          x = x0;
          y = y0;

          // draw the resulting point
          if (Thread.currentThread() == thread) {
//         	 if (img != null) {
//         		 super.getGraphics().drawImage(img, 0, 0, Color.black, this);
//       		}
         	 drawer.point(x, y);
          }
//          StdDraw.point(x, y); 

          // for efficiency, display only every 100 iterations
          if (i % 250 == 0) { 
//         	 StdDraw.show(10);
//         	 super.getGraphics().drawImage(img, 0, 0, Color.black, this);
//         	 try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
         	 drawer.show(10);
			}

			if (i % (iterations / 5) == 0) {
				System.out.print(i + " ");
				jTextArea.setText(jTextArea.getText()+" "+i+" ");
			}
		}
//      label.setText(i+" ");
      jTextArea.setText(jTextArea.getText()+" "+i+" ");
		System.out.println(i);
   }*/
	
	private double[] getDist() {
  	 double[] dist = { 0.01, 0.85, 0.07, 0.07 };
  	 return dist;
   }
   private double[][] getCx() {
		double[][] cx = {
			{ 0.00, 0.00, 0.500 },
			{ 0.85, 0.04, 0.075 },
			{ 0.20, -0.26, 0.400 },
			{ -0.15, 0.28, 0.575 }, };
		return cx;
   }
   private double[][] getCy() {
		double[][] cy = {
			{ 0.00, 0.16, 0.000 }, { -0.04, 0.85, 0.180 }, { 0.23, 0.22, 0.045 }, { 0.26, 0.24, -0.086 } };
		return cy;
   }
   
   static class JTextAreaPrinter implements Printer {
   	private JTextArea jTextArea;
		public JTextAreaPrinter(JTextArea jTextArea) {
			this.jTextArea = jTextArea;
		}
		@Override
		public void print(String string) {
			jTextArea.append(string);
		}
		@Override
		public void println(String string) {
			jTextArea.append(string+"\n");
		}
	}

}
