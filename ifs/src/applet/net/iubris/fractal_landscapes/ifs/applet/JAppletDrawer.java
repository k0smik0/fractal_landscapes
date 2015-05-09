package net.iubris.fractal_landscapes.ifs.applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;

import net.iubris.fractal_landscapes.ifs.common.Drawer;

public class JAppletDrawer implements Drawer {
		private final BufferedImage offscreenImage, onscreenImage;
		private final Graphics2D offscreen, onscreen;
		private final Color penColor;
		private final double penRadius;
//		private final JFrame frame;

		private static final double DEFAULT_XMIN = 0.0;
		private static final double DEFAULT_XMAX = 1.0;
		private static final double DEFAULT_YMIN = 0.0;
		private static final double DEFAULT_YMAX = 1.0;
		private static final double BORDER = 0.00;
		private final int DEFAULT_SIZE = 512;
//		private final int width = DEFAULT_SIZE;
//		private final int height = DEFAULT_SIZE;
		private final double DEFAULT_PEN_RADIUS = 0.002;
		private final Color DEFAULT_PEN_COLOR = Color.BLACK;
		private final Color DEFAULT_CLEAR_COLOR = Color.WHITE;
		private final Object mouseLock = new Object();
		
		private boolean defer = false;
		private double xmin, ymin, xmax, ymax;
		
		private final JApplet jApplet;
		private final int width;
		private final int height;
   	 
   	 public JAppletDrawer(JApplet jApplet, int width, int height) {
   		 this.jApplet = jApplet;
   		 this.width = width+50;
   		 this.height = height-20;
   		 offscreenImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
          onscreenImage  = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
          offscreen = offscreenImage.createGraphics();
          onscreen  = onscreenImage.createGraphics();
          
          setXscale();
          setYscale();
          offscreen.setColor(DEFAULT_CLEAR_COLOR);
          offscreen.fillRect(0, 0, width, this.height);
          this.penColor = setPenColor();
          this.penRadius = setPenRadius();
          clear();
          
       // add antialiasing
          RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          offscreen.addRenderingHints(hints);
		}
   	 
   	 @Override
   	 public void clear() { clear(DEFAULT_CLEAR_COLOR); }
   	 private void clear(Color color) {
          offscreen.setColor(color);
          offscreen.fillRect(0, 0, width, height);
          offscreen.setColor(penColor);
          draw();
      }
   	 
   	 @Override
       public void point(double x, double y) {
          double xs = scaleX(x);
          double ys = scaleY(y);
          double r = penRadius;
          float scaledPenRadius = (float) (r * DEFAULT_SIZE);

          // double ws = factorX(2*r);
          // double hs = factorY(2*r);
          // if (ws <= 1 && hs <= 1) pixel(x, y);
          if (scaledPenRadius <= 1) { 
         	 pixel(x, y);
          } else {
         	 offscreen.fill(new Ellipse2D.Double(xs - scaledPenRadius/2, ys - scaledPenRadius/2, scaledPenRadius, scaledPenRadius));
          }
//          System.out.println("drawing");
          draw();
      }
       private void pixel(double x, double y) {
          offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
      }
       private double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
       private double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }
       
       

		private void setXscale() {
			setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
		}
		private void setXscale(double min, double max) {
			double size = max - min;
			synchronized (mouseLock) {
				xmin = min - BORDER * size;
				xmax = max + BORDER * size;
			}
		}
		private void setYscale() {
			setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
		}

		private void setYscale(double min, double max) {
			double size = max - min;
			synchronized (mouseLock) {
				ymin = min - BORDER * size;
				ymax = max + BORDER * size;
			}
		}

      private Color setPenColor() { 
      	return setPenColor(DEFAULT_PEN_COLOR); 
      }
      private Color setPenColor(Color color) {
          offscreen.setColor(color);
          return color;
      }
      
      private double setPenRadius() { 
      	return setPenRadius(DEFAULT_PEN_RADIUS); 
   	}
      private double setPenRadius(double r) {
          if (r < 0) 
         	 throw new IllegalArgumentException("pen radius must be nonnegative");
          
//          penRadius = r;
          float scaledPenRadius = (float) (r * DEFAULT_SIZE);
          BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
          // BasicStroke stroke = new BasicStroke(scaledPenRadius);
          offscreen.setStroke(stroke);
          return r;
      }
      
      @Override
      public void show(int t) {
         defer = false;
         draw();
         try { 
         	Thread.sleep(t); 
         } catch (InterruptedException e) { 
         	System.out.println("Error sleeping"); 
      	}
         defer = true;
     }
      
      private void draw() {
         if (defer) {
         	return;
         }
         onscreen.drawImage(offscreenImage, 0, 0, null);
//         frame.repaint();
         jApplet.repaint();
     }
      
      public BufferedImage getOnscreenImage() {
			return onscreenImage;
		}
      
      public BufferedImage getOffscreenImage() {
			return offscreenImage;
		}
	} // end Drawer