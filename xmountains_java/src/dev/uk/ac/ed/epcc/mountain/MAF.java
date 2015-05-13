package uk.ac.ed.epcc.mountain;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MAF {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
			try {
				MAF window = new MAF();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MAF() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setSize(430, 280);
		
		frame.getContentPane().add(lblNewLabel);
	}

}
