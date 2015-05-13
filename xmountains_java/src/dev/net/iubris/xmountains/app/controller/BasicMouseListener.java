package net.iubris.xmountains.app.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.iubris.xmountains.app.view.MountAppFrame;

public class BasicMouseListener implements MouseListener {

	private final MountAppFrame mountAppFrame;

	private boolean frozen = false;
	
	private MountAppController mountAppController;

	public BasicMouseListener(MountAppFrame mountAppFrame/*, MountAppController mountAppController*/) {
		this.mountAppFrame = mountAppFrame;
//		this.mountAppController = mountAppController;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (frozen) {
			frozen = false;
			mountAppFrame.showStatus("mountapp restarted");
			mountAppController.start();
		} else {
			frozen = true;
			mountAppFrame.showStatus("mountapp paused");
			mountAppController.stop();
		}
	}
	
	public boolean isFrozen() {
			return frozen;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

	public void setController(MountAppController mountAppController) {
		this.mountAppController = mountAppController;
	}
}