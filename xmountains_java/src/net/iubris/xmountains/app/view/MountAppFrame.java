package net.iubris.xmountains.app.view;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MountAppFrame extends JFrame /*implements Runnable*//*, MouseListener*//*, DummyApplet*/ {

	private static final long serialVersionUID = 532538699460102929L;
	
	/*
	 * model zone - start
	 */
	private final int width;
	private final int height;
	
//	private Artist art;
//	private Mountain mountain;
	
	/*int levels = 10;
	int stop = 2;
	double fdim = 0.65;
//	boolelan frozen = false;
	long little_sleep = 100;
	long long_sleep = 5000;*/

	private Image img;
	private JLabel statusLabel;
	
//	private Thread t;
//	private final Parameter parameter;
	
//	private static MountAppFrame instance;
//	public static MountAppFrame getInstance(int width, int height) {
//		if (instance==null) {
//			instance = new MountAppFrame(width, height);
//		}
//		return instance;
//	};
	
//	private
	public 
	MountAppFrame(/*MouseListener mouseListener,*/ /*Parameter parameter,*/ int width, int height) {
		super();
		setSize(512, 384);
		getContentPane().setLayout( new FlowLayout() );
//		setResizable(false);
		this.width = width;
		this.height = height;
//		this.parameter = parameter;
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		
		setTitle("xmountains");
		
//		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
		
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		addMouseListener(mouseListener);
		
//		img = createImage(width-10, height-10);
//		img = new BufferedImage(width-10, height-10, BufferedImage.TYPE_INT_RGB);
//		System.out.println(img);
//		getContentPane().add( new JLabel(new ImageIcon( img )) );
//		statusLabel = new JLabel("status");
//		getContentPane().add( statusLabel );
		
		setVisible(true);
		pack();
		repaint();
	}
	
	public void init() {
		img = createImage(width-10, height-10);
		getContentPane().add( new JLabel(new ImageIcon( img )) );
		statusLabel = new JLabel("status");
		getContentPane().add( statusLabel );
	}
	
	public Graphics getGraphics() {
		return img.getGraphics();
	}
	
	/*
	 * DummyApplet zone - start
	 */
//	@Override
//	public void init() {
		
//		img = createImage(width-10, height-10);
//		getContentPane().add( new JLabel(new ImageIcon( img )) );
		
		/*setLevels();
		setStop();
		setFdim();
		setSleep();
		setSnooze();
		setSize(width, height);

		mountain = new Mountain();
		mountain.set_fdim(fdim);
		mountain.set_size(levels, stop);
		mountain.init();
//		System.out.println(width+" "+height);
		
		Graphics g = img.getGraphics();
		art = new Artist(width-10, height-10, g, mountain);*/
		
//		art.init_artist_variables();
//		addMouseListener(this);
//	}
	
	/*private void setLevels() {
		String s = getParameter("levels");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			if (i > 1) {
			levels = i;
			}
		}
	}
	private void setStop() {
		String s = getParameter("stop");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			if (i > 1) {
			stop = i;
			}
		}
	}
	private void setFdim() {
		String s = getParameter("fdim");
		double tmp;
		Double dble;
		if (s != null) {
			dble = new Double(s);
			tmp = dble.doubleValue();
			if (tmp > 0.5 && tmp <= 1.0) {
			fdim = tmp;
			}
		}
	}
	private void setSleep() {
		String s = getParameter("sleep");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			long_sleep = (long) i;
		}
	}
	private void setSnooze() {
		String s = getParameter("snooze");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			little_sleep = (long) i;
		}
	}*/

	/*@Override
	public void start() {
		System.out.println("start called");
		if (frozen) {
			// Do nothing motion is stopped
		} else {
			// start animating
			if (t == null) {
				t = new Thread(this);
			}
			t.start();
		}
	}*/

	/*@Override
	public void stop() {
		 System.out.println("stop called");
		t = null;
	}*/
	
	/*@Override
	public void run() {
		// System.out.println("run called");
		long snooze = 0;
		long target_time;

		// Just to be nice, lower this thread's priority
		// so it can't interfere with other processing going on.
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//		while (Thread.currentThread() == t) {
		while (true) {
			target_time = System.currentTimeMillis();
			art.plot_column();
			repaint();
			// attempt to subract the update time from the sleep time
			// to give a constant update rate. If the update takes too long
			// things will of course be slower.
			if (art.scroll != 0) {
				// snooze=long_sleep;
				snooze = target_time + long_sleep - System.currentTimeMillis();
			} else {
				// snooze=little_sleep;
				snooze = target_time + little_sleep - System.currentTimeMillis();
			}
			if (snooze > 0) {
				// try{Thread.currentThread().sleep(snooze);}catch (InterruptedException e){}
				try {
					Thread.sleep(snooze);
				} catch (InterruptedException e) {}
			} else {
				// let other threads hava a go.
				// Thread.currentThread().yield();
				Thread.yield();
			}
		}
	}*/
	
	/*
	 * workaround for use old code from applet
	 */
	/*@Override
	public String getParameter(String parameterName) {
		try {
			Field field = parameter.getClass().getDeclaredField(parameterName);
			field.setAccessible(true);
			return (String) field.get(parameter);
		} catch(NoSuchFieldException e) {
//			e.printStackTrace();
			System.err.println("no field «"+parameterName+"»");
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
//	@Override
	public void showStatus(String status) {
		System.out.println(status);
		statusLabel.setText(status);
	}
	/*
	 * DummyApplet zone - end
	 */

}
