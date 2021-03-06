package cs32.maps.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.Thread.State;
import java.util.List;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import Autocomplete.AutocompleteEngine;
import cs32.maps.MapsEngine;

public class OptionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private MapsEngine _engine;
	private MapPanel _map;
	private AutocompleteEngine _ac;
	private MapsGUI _gui;
	private DefaultListModel<String> listModel;
	private JList<String> suggestionList;
	private JTextField street1Box;
	private JTextField street2Box;
	private JButton clearButton;
	private JButton directionsButton;
	private int _currentBox = 1;
	private boolean _isShifting = false;
	private PathRequestThread _prthread;
	
	/*
	 * option panel is contains text input and buttons for getting a shortest path and clearing the screen.
	 */
	
	public OptionPanel(MapsEngine en, MapsGUI gui, MapPanel mp) { //also needs to know about Autocomplete engine
		_engine = en;
		_gui = gui;
		_map = mp;
		_ac = new AutocompleteEngine();
	
		Set<String> names = _engine.getStreetNames();
		_ac.addAllWords(names);
        
		//new string model for the autocomplete suggestions
        listModel = new DefaultListModel<String>();
        
        //autocomplete suggestions are presented in a list.
        suggestionList = new JList<String>(listModel);
        suggestionList.setVisible(false);
        suggestionList.setPreferredSize(new Dimension(220,100));
        suggestionList.setLayoutOrientation(JList.VERTICAL);
        suggestionList.setVisibleRowCount(AutocompleteEngine.SUGGS); 
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        //JTextFields for the text input and JLabels to display what they are for.
        JLabel street1Label = new JLabel("Street 1:");
        JLabel street2Label = new JLabel("Street 2:");
        street1Box = new JTextField(20);
        street2Box = new JTextField(20);
        
        //Swing elements are aligned using multiple JPanels and layout managers.
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JPanel street1Panel = new JPanel();
        street1Panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel street2Panel = new JPanel();
        street2Panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        //intersection buttons allow users to set location and destination nodes using streetnames.
        JButton intersection1Button = new JButton("Set Location");
        intersection1Button.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton intersection2Button = new JButton("Set Destination");      
        intersection2Button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        street1Panel.add(street1Label);
        street1Panel.add(street1Box);
        street2Panel.add(street2Label);
        street2Panel.add(street2Box);
        
        boxPanel.add(street1Panel);
        boxPanel.add(street2Panel);
        boxPanel.add(intersection1Button);
        boxPanel.add(Box.createRigidArea(new Dimension(0,10)));
        boxPanel.add(intersection2Button);
        this.add(boxPanel);
        this.add(suggestionList);
        
        //clearButton and directionsButton for clearing the user input and creating a shortest path request.
        clearButton = new JButton("Clear");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        directionsButton = new JButton("Get Directions");
        directionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(directionsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0,10)));
        buttonPanel.add(clearButton);
        this.add(Box.createRigidArea(new Dimension(20,0)));
        this.add(buttonPanel);
        
		//DocumentListener that calls a method every time the input inside _textBox
		//is changed.
        street1Box.getDocument().addDocumentListener(new BoxDocumentListener(1));        
        street1Box.addKeyListener(new BoxKeyListener(1));
        street1Box.addFocusListener(new BoxFocusListener(1));
        
        street2Box.getDocument().addDocumentListener(new BoxDocumentListener(2)); 
        street2Box.addKeyListener(new BoxKeyListener(2));
        street2Box.addFocusListener(new BoxFocusListener(2));
        
        //multiple focus listeners allows us to implement an autocomplete suggestions list that disappears when not in use. 
        suggestionList.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				_isShifting = false;
			}

			@Override
			public void focusLost(FocusEvent e) {}
        	
        });
        
        //users need to be able to navigate the list using keyboard inputs.
        //pressing ENTER should transfer the selected item to the textField
        suggestionList.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					if (_currentBox == 1) {
						street1Box.setText(suggestionList.getSelectedValue());
						street2Box.requestFocus();
					}
					else {
						street2Box.setText(suggestionList.getSelectedValue());
						directionsButton.requestFocus();
					}
					listModel.clear();
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {}
        	
        });
        
        //similarly, users should also be able to transfer a selection to a textField by double clicking on the list.
        suggestionList.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				_isShifting = true;
				if (e.getClickCount() == 2) {
					String string = suggestionList.getSelectedValue();
					if (_currentBox == 1) {
						street1Box.setText(string);
						street2Box.requestFocus();
					}
					else {
						street2Box.setText(string);
						directionsButton.requestFocus();
					}
					listModel.clear();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
        	
        });
        
        //clearButton resets all the user information in GUI when clicked. 
        clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.clear();
				_gui.setCurrentLocation(null);
				_gui.setDestination(null);
				_gui.setPath();
				street1Box.setText("");
				street2Box.setText("");
				_map.repaint();
			}
        	
        });
        
        //directionsButton creates a new thread to retrieve the shortest path.
        directionsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){
				if (_prthread == null || _prthread.getState() == State.TERMINATED) {
					_prthread = new PathRequestThread(_gui);
					_prthread.start();
				}
				else {
					System.out.println("Warning: Previous Search is not complete");
				}
			}
        	
        });
        
        //intersection buttons use getNodeFromIntersection methods to select a node using two street names.
        intersection1Button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {				
				_gui.setCurrentLocation(_engine.getNodeFromIntersection(street1Box.getText(),street2Box.getText()));
				street1Box.setText(null);
				street2Box.setText(null);
				_map.repaint();
			}
        	
        });
        
        intersection2Button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {				
				_gui.setDestination(_engine.getNodeFromIntersection(street1Box.getText(),street2Box.getText()));
				street1Box.setText(null);
				street2Box.setText(null);
				_map.repaint();
			}
        	
        });
	}
	
	private class BoxDocumentListener implements DocumentListener{
		private int _boxNo;
		public BoxDocumentListener(int i) {
			super();
			_boxNo = i;
		}
		
		@Override
    	public void removeUpdate(DocumentEvent e){
    		getSuggestions(_boxNo);
    	}
		
		@Override
    	public void insertUpdate(DocumentEvent e){
    		getSuggestions(_boxNo);
    	}
		
		@Override
    	public void changedUpdate(DocumentEvent e){
    		getSuggestions(_boxNo);
    	}
			    
		//getSuggestions uses autocorrect's printSuggestions method to get a List of 
		//suggestions and then calls listModel's addElement method to update the suggestions
		//that are currently being displayed.
	}
	
	private void getSuggestions(int boxNo){
		listModel.clear();
		String input;
		if (boxNo == 1) input = street1Box.getText();
		else input = street2Box.getText();
		if (input.length() > 0) {
			suggestionList.setVisible(true);
			List<String> toPrint = _ac.getCompletions(input);
			for (String s : toPrint){
				listModel.addElement(s);
			}
		}
			
	}
	
	//pressing up or down while typing in a TextField should shift the focus to the autocorrect list.
	private class BoxKeyListener implements KeyListener {
		private int _boxNo;
		public BoxKeyListener(int i) {
			super();
			_boxNo = i;
		}
		
		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN){
				_isShifting = true;
				suggestionList.requestFocus();
				suggestionList.setSelectedIndex(0);
			}
			
			else if (key == KeyEvent.VK_ENTER) {
				if (_boxNo == 1) street2Box.requestFocus();
				else directionsButton.requestFocus();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
		
	}
	
	private class BoxFocusListener implements FocusListener{
		private int _boxNo;
		public BoxFocusListener(int i) {
			super();
			_boxNo = i;
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			_isShifting = false;
			_currentBox = _boxNo;
			getSuggestions(_boxNo);
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (!_isShifting) suggestionList.setVisible(false);
		}
	}
}
