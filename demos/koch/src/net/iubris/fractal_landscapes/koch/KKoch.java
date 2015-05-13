/*
 * from http://sourcecodesforfree.blogspot.it/2013/05/32-koch-curve.html
 */
package net.iubris.fractal_landscapes.koch;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class KKoch extends Applet implements MouseListener {

	private static final long serialVersionUID = 2099219147789521887L;

	private int iteration;
	private int size = 20;

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		Dimension w = getSize();
		koch(size * 3, w.height - size * 7, w.width - size * 3, w.height - size * 7, iteration, g);
		koch(w.width / 2, size * 2, size * 3, w.height - size * 7, iteration, g);
		koch(w.width - size * 3, w.height - size * 7, w.width / 2, size * 2, iteration, g);
	}

	private void koch(double x1, double y1, double x2, double y2, int iteration, Graphics g) {
		if (iteration <= 1) { // draw first line
			g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		} else {
			// System.out.println(iteration);
			double x4 = x1 * 2 / 3 + x2 * 1 / 3;
			double y4 = y1 * 2 / 3 + y2 * 1 / 3;
			double x5 = x1 * 1 / 3 + x2 * 2 / 3;
			double y5 = y1 * 1 / 3 + y2 * 2 / 3;
			double x6 = (int) (0.5 * (x1 + x2) + Math.sqrt(3) * (y1 - y2) / 6);
			double y6 = (int) (0.5 * (y1 + y2) + Math.sqrt(3) * (x2 - x1) / 6);
			// call itself on intermediate points, until iteration reachs 0
			koch(x1, y1, x4, y4, iteration - 1, g);
			koch(x4, y4, x6, y6, iteration - 1, g);
			koch(x6, y6, x5, y5, iteration - 1, g);
			koch(x5, y5, x2, y2, iteration - 1, g);
		}
	}

	@Override
	public void init() {
		setBackground(Color.white);
		addMouseListener(this);
		iteration = 2;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		iteration++;
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}