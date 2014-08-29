package simpleSwingDemo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField	textField;
	JButton		button;
	
	
	SingleButtonPanel(){
		textField	= new JTextField();
		button		= new JButton();
		
		textField.setPreferredSize(new Dimension(150, 25));
		button.setText("Hit me!");
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textField.setText(textField.getText().concat("|"));
				
			}
		});
		
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
