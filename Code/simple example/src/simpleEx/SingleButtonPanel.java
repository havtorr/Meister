package simpleEx;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Simple  GUI-example with a single button and a listener
 * @author Håvard Tørresen
 *
 */
public class SingleButtonPanel extends JPanel{
	
	JTextField	textField;
	JButton		button;
	
	
	SingleButtonPanel(){
		textField	= new JTextField();
		button		= new JButton();
		
		textField.setPreferredSize(new Dimension(150, 25));
		button.setText("Hit me!");
		
		add(textField);
		add(button);
	}
	
	
	
	public static void main(String[] args) {
		JFrame				frame	= new JFrame();
		SingleButtonPanel	panel	= new SingleButtonPanel();
		
		frame.setContentPane(panel);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		
	}
}
