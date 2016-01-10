import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Options extends JFrame {
	private static final long serialVersionUID = -4274195600479545619L;
	
	Main main;
	public Options(Main main) {
		super("Options");
		this.main = main;
		buildWindow();
		pack();
		setResizable(false);
		addWindowListener(new MyWindowAdapter());
		setVisible(true);
	}
	
	JRadioButton beginner, intermediate, expert, custom;
	ButtonGroup radioGroup;
	JTextField customHeight, customWidth, customMines;
	JButton ok;
	
	private void buildWindow() {
		setLayout(new GridLayout(6, 4));
		radioGroup = new ButtonGroup();
		
		//row 1
		add(new JLabel());
		add(new JLabel("Height"));
		add(new JLabel("Width"));
		add(new JLabel("Mines"));
		
		//row 2
		beginner = new JRadioButton("Beginner");
		if (main.difficulty == 0) {
			beginner.setSelected(true);
		}
		radioGroup.add(beginner);
		add(beginner);
		add(new JLabel(main.beginner[0] + ""));
		add(new JLabel(main.beginner[1] + ""));
		add(new JLabel(main.beginner[2] + ""));
		
		//row 3
		intermediate = new JRadioButton("Intermediate");
		if (main.difficulty == 1) {
			intermediate.setSelected(true);
		}
		radioGroup.add(intermediate);
		add(intermediate);
		add(new JLabel(main.intermediate[0] + ""));
		add(new JLabel(main.intermediate[1] + ""));
		add(new JLabel(main.intermediate[2] + ""));
		
		//row 4
		expert = new JRadioButton("Expert");
		if (main.difficulty == 2) {
			expert.setSelected(true);
		}
		radioGroup.add(expert);
		add(expert);
		add(new JLabel(main.expert[0] + ""));
		add(new JLabel(main.expert[1] + ""));
		add(new JLabel(main.expert[2] + ""));
		
		//row 5
		custom = new JRadioButton("Custom");
		if (main.difficulty == 3) {
			custom.setSelected(true);
		}
		radioGroup.add(custom);
		add(custom);
		customHeight = new JTextField(main.custom[0] +"");
		add(customHeight);
		customWidth = new JTextField(main.custom[1] + "");
		add(customWidth);
		customMines = new JTextField(main.custom[2] + "");
		add(customMines);
		
		//row 6
		add(new JLabel());
		add(new JLabel());
		add(new JLabel());
		ok = new JButton("OK");
		ok.addActionListener(new MyOptionsButtonListener());
		add(ok);
	}
	
	private class MyOptionsButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Object source = arg0.getSource();
			
			if (source.equals(ok)) {
				Options.this.dispatchEvent(new WindowEvent(Options.this, WindowEvent.WINDOW_CLOSING));
				//TODO input validation
				main.custom = new int[]{Integer.parseInt(customHeight.getText()), Integer.parseInt(customWidth.getText()), Integer.parseInt(customMines.getText())};
			}
			
		}
		
	}
	
	private class MyWindowAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent windowEvent) {
			main.setEnabled(true);
			if (beginner.isSelected()) {
				main.difficulty = 0;
			}
			else if (intermediate.isSelected()) {
				main.difficulty = 1;
			}
			else if (expert.isSelected()) {
				main.difficulty = 2;
			}
			else if (custom.isSelected()) {
				main.difficulty = 3;
				//TODO make sure mines is < height * width - 1
			}
			main.custom[0] = Integer.parseInt(customHeight.getText());
			main.custom[1] = Integer.parseInt(customWidth.getText());
			main.custom[2] = Integer.parseInt(customMines.getText());
		}
	}
}