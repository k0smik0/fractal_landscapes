/**
 *  File: Koch.java
 *  Author: Bill Tschumy
 *
 *  This class draws a Koch snowflake using the Koch curve fractal.  More informtion
 *  on this fractal may be found at:
 *
 *  http://ecademy.agnesscott.edu/~lriddle/ifs/kcurve/kcurve.htm
 *
 *  This Turtle also shows an example of how to introduce GUI elements into the 
 *  turtle's display.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.otherwise.jurtle.*;

public class Koch extends Turtle implements ActionListener
{
    private JButton drawBtn;
    private JTextField sizeTF;
    private JTextField recurseTF;
    private int size = 300;
    private int numRecursions = 4;

    public void runTurtle()
    {
        setAutoUpdate( false );

        // Initialize the GUI
        JPanel displayPanel = getDisplay();
        displayPanel.setLayout( new FlowLayout() );

        JLabel lable = new JLabel( "Size:" );
        displayPanel.add( lable );

        sizeTF = new JTextField( 4 );
        sizeTF.setText( Integer.toString( size ) );
        displayPanel.add( sizeTF );
        sizeTF.addActionListener( this );

        JLabel lbl = new JLabel( "     Number of recursions:" );
        displayPanel.add( lbl );

        recurseTF = new JTextField( 4 );
        recurseTF.setText( Integer.toString( numRecursions ) );
        displayPanel.add( recurseTF );
        recurseTF.addActionListener( this );

        drawBtn = new JButton( "Draw" );
        if ( System.getProperty( "os.name" ).indexOf( "Mac OS" ) >= 0 )
            drawBtn.setOpaque( false );
        displayPanel.add( drawBtn );
        drawBtn.addActionListener( this );

        // Validate the panel to properly lay out the components
        displayPanel.validate();

        // Wait for input from the GUI
        waitForStop();
    }


    /**
     *  Called when the Draw button is clicked or Enter is hit in
     *  the textfields.
     */
    public void actionPerformed( ActionEvent evt )
    {
        // Get the current paramenter values
        try
        {
            size = Integer.parseInt( sizeTF.getText() );
            numRecursions = Integer.parseInt( recurseTF.getText() );
        }
        catch ( NumberFormatException e )
        {}

        // Spawn a separate thread to do the drawing in.  If you
        // draw in the event thread the incremental auto-update
        // mechanism won't work.  Everything will be drawn at once.

        Thread drawThread = new Thread()
                            {
                                public void run()
                                {
                                    drawSnowflake();
                                }
                            };

        drawThread.start();

    }


    /**
     *  Draws the Koch snowflake by calling drawSegment for 
     *  each leg of the equilateral triangle.
     */
    private void drawSnowflake()
    {
        pause( 100 );
        clearDisplay();
        home();
        hideTurtle();
        Dimension displaySize = getDisplaySize();

        // Position the turtle for the initial drawing
        penUp();
        setPosition( ( displaySize.width - size ) / 2,
                     size + ( displaySize.height - ( int ) ( size * 1.3 ) ) / 2 );
        penDown();

        // Draw the first segment of the equilateral triangle
        right( 30 );
        drawSegment( size );

        // Draw the second segment of the equilateral triangle
        right( 120 );
        drawSegment( size );

        // Draw the third segment of the equilateral triangle
        right( 120 );
        drawSegment( size );

        updateDisplay();
    }

   
    /**
     *  Recursively draws a segment of the Koch curve with length "length".
     */
    private void drawSegment( double length )
    {
        numRecursions--;
        penDown();
        if ( numRecursions == 0 )
            forward( length );
        else
        {
            setPenColor( Color.blue );
            drawSegment( length / 3 );
            left( 60 );
            setPenColor( Color.red );
            drawSegment( length / 3 );
            right( 120 );
            drawSegment( length / 3 );
            left( 60 );
            setPenColor( Color.blue );
            drawSegment( length / 3 );
            if ( length > 12 )
                updateDisplay();
        }
        numRecursions++;
    }


}
