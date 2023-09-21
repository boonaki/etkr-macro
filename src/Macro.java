import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Macro {
	static boolean enabled = false;
	static boolean findingCoords = false;
	static boolean findingWhichCoords = false;
	static int startXCord;
	static int yCord;
	static int endCord;
	static JLabel startCordText;
	static JLabel startYCordText;
	static JLabel endCordText;
	static JLabel xDragDistance;
	static JLabel yDragDistance;
	static int xDragDistanceVar;
	static int xDistanceSwiped = 0;
	static int yDistanceSwiped = 0;
	static int yDragDistanceVar = 200;
	JTextField searchPatternWidthField;
	JTextField searchPatternHeightField;
	JLabel searchPatternWidthValueLabel;
	JLabel searchPatternHeightValueLabel;
	static int searchPatternYSwipeCount;
	static int searchPatternXSwipeCount;
	static boolean isXDirectionFlipped = false;
	static boolean isYDirectionFlipped = false;
	JButton setWidth;
	JButton setHeight;
	JCheckBox checkRepeat;
	static boolean isRepeating = false;
	RecordPositionByClickListener recordPositionlistener = new RecordPositionByClickListener();
	StopMacroListener stopListener = new StopMacroListener();

	public void start() {
		setUpGui();
		try {
			Robot r = new Robot();
			int button = InputEvent.BUTTON1_DOWN_MASK;
			int current = 0;
			while (true) {
				if (enabled) {
					while (yDistanceSwiped < searchPatternYSwipeCount) {
						while (xDistanceSwiped < searchPatternXSwipeCount) {
							if (isXDirectionFlipped) {
								r.mouseMove(endCord, yCord);
								r.mousePress(button);
								current = 0;
								while (current != xDragDistanceVar) {
									if (startXCord > endCord) {
										r.mouseMove(endCord + current, yCord);
									} else {
										r.mouseMove(endCord - current, yCord);
									}
									current++;
									Thread.sleep(4);
								}
								r.mouseRelease(button);
								xDistanceSwiped = xDistanceSwiped + 1;
							} else {
								r.mouseMove(startXCord, yCord);
								r.mousePress(button);
								current = 0;
								while (current != xDragDistanceVar) {
									if (startXCord > endCord) {
										r.mouseMove(startXCord - current, yCord);
									} else {
										r.mouseMove(startXCord + current, yCord);
									}
									current++;
									Thread.sleep(4);
								}
								r.mouseRelease(button);
								xDistanceSwiped = xDistanceSwiped + 1;
							}
							Thread.sleep(500);
						}
						if (isYDirectionFlipped) {
							r.mouseMove(startXCord, yCord);
							r.mousePress(button);
							current = 0;
							while (current < 200) {
								r.mouseMove(startXCord, yCord + current);
								current++;
								Thread.sleep(4);
							}
						} else {
							r.mouseMove(startXCord, 760);
							r.mousePress(button);
							current = 0;
							while (current < 200) {
								r.mouseMove(startXCord, (760 - current));
								current++;
								Thread.sleep(4);
							}
						}
						r.mouseRelease(button);
						yDistanceSwiped = yDistanceSwiped + 1;
						xDistanceSwiped = 0;
						isXDirectionFlipped = !isXDirectionFlipped;
						Thread.sleep(400);
					}
					if (isRepeating) {
						// find where x
						yDistanceSwiped = 0;
						isYDirectionFlipped = !isYDirectionFlipped;
					} else {
						enabled = false;
						xDistanceSwiped = 0;
						yDistanceSwiped = 0;
					}
				}
				Thread.sleep(1500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUpGui() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Boon ETKR Mouse Macro");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		JSeparator separatorTwo = new JSeparator(JSeparator.HORIZONTAL);
		JPanel innerPanelCenter = new JPanel();
		JPanel innerPanelBottom = new JPanel();
		JPanel innerPanelCenterButtons = new JPanel();
		JPanel innerPanelBottomText1 = new JPanel();
		JPanel innerPanelBottomText2 = new JPanel();

		innerPanelCenterButtons.setLayout(new GridLayout(1, 0));

		searchPatternWidthField = new JTextField("");
		searchPatternWidthField.setPreferredSize(new Dimension(125, 25));
		searchPatternWidthField.setEnabled(false);

		searchPatternHeightField = new JTextField("");
		searchPatternHeightField.setPreferredSize(new Dimension(125, 25));
		searchPatternHeightField.setEnabled(false);

		searchPatternWidthValueLabel = new JLabel();
		searchPatternHeightValueLabel = new JLabel();

		startYCordText = new JLabel("");
		JLabel searchPatternWidthLabel = new JLabel("Horizontal Swipe Count: ");
		JLabel searchPatternHeightLabel = new JLabel("Vertical Swipe Count: ");
		startCordText = new JLabel("Starting x-axis Coordinate: ");
		endCordText = new JLabel("End x-axis Coordinate: ");
		xDragDistance = new JLabel();
		yDragDistance = new JLabel("Y-Drag Distance: 200");
		yDragDistance.setVisible(false);

		checkRepeat = new JCheckBox("Repeat Infinitely");
		checkRepeat.addItemListener(new CheckRepeatListener());

		JButton startMacroButton = new JButton("Start Mouse Macro");
		startMacroButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		startMacroButton.addActionListener(new StartMacroListener());

		JButton startFindCoord = new JButton("Find starting position");
		JButton endFindCoord = new JButton("Find end position");
		PlainDocument doc = (PlainDocument) searchPatternWidthField.getDocument();
		doc.setDocumentFilter(new IntFilter());

		setWidth = new JButton("Set Count");
		setWidth.addActionListener(new SetSearchWidthValue());
		setWidth.setEnabled(false);
		setHeight = new JButton("Set Count");
		setHeight.addActionListener(new SetSearchHeightValue());
		setHeight.setEnabled(false);

		startFindCoord.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		endFindCoord.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		innerPanelBottom.setLayout(new FlowLayout(FlowLayout.LEFT));
		innerPanelCenterButtons.setLayout(new FlowLayout(FlowLayout.LEFT));

		startFindCoord.addActionListener(new FindStartCoordListener());
		endFindCoord.addActionListener(new FindEndCoordListener());
		startFindCoord.setPreferredSize(new Dimension(125, 35));
		endFindCoord.setPreferredSize(new Dimension(125, 35));
		innerPanelCenterButtons.add(startFindCoord);
		innerPanelCenterButtons.add(endFindCoord);

		innerPanelCenter.add(startYCordText);
		innerPanelCenter.add(innerPanelCenterButtons);
		innerPanelCenter.add(startCordText);
		innerPanelCenter.add(endCordText);
		innerPanelCenter.add(xDragDistance);
		innerPanelCenter.add(yDragDistance);

		innerPanelBottomText1.add(searchPatternWidthLabel);
		innerPanelBottomText1.add(searchPatternWidthField);
		innerPanelBottomText1.add(setWidth);
		innerPanelBottomText2.add(searchPatternHeightLabel);
		innerPanelBottomText2.add(searchPatternHeightField);
		innerPanelBottomText2.add(setHeight);

		innerPanelBottom.add(checkRepeat);
		innerPanelBottom.add(innerPanelBottomText1);
		innerPanelBottom.add(innerPanelBottomText2);
		innerPanelBottom.add(searchPatternWidthValueLabel);
		innerPanelBottom.add(searchPatternHeightValueLabel);

		panel.add(innerPanelCenter);
		panel.add(separatorTwo);
		panel.add(innerPanelBottom);

		panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

		frame.getContentPane().add(BorderLayout.CENTER, panel);
		frame.getContentPane().add(BorderLayout.SOUTH, startMacroButton);
		frame.setSize(400, 450);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		Macro app = new Macro();
		app.start();
	}

	public void detach() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(recordPositionlistener);
		findingCoords = false;
		System.out.println("Detached the Listener!");
	}

	class FindStartCoordListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("starting to find start coords");
			findingCoords = true;
			findingWhichCoords = true;
			Toolkit.getDefaultToolkit().addAWTEventListener(recordPositionlistener, AWTEvent.FOCUS_EVENT_MASK);
		}
	}

	class FindEndCoordListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("starting to find end coords");
			findingCoords = true;
			findingWhichCoords = false;
			Toolkit.getDefaultToolkit().addAWTEventListener(recordPositionlistener, AWTEvent.FOCUS_EVENT_MASK);
		}
	}

	class CheckRepeatListener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			if (checkRepeat.isSelected()) {
				isRepeating = true;
			} else {
				isRepeating = false;
			}
		}
	}

	class StartMacroListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
//			if (startXCord < 0 || endCord < 0 || searchPatternXSwipeCount < xDragDistanceVar
//					|| searchPatternYSwipeCount < yDragDistanceVar) {
//				System.out.println("failed to start macro");
//				return;
//			}
			if (startXCord < 0 || endCord < 0) {
				System.out.println("failed to start macro");
				return;
			}
			enabled = !enabled;
			yDistanceSwiped = 0;
			xDistanceSwiped = 0;
			System.out.println("Started the macro");
			if (!GlobalScreen.isNativeHookRegistered()) {
				try {
					GlobalScreen.registerNativeHook();
				} catch (NativeHookException ex) {
					System.err.println("There was an error registering the native hook");
					System.err.println(ex.getMessage());

					System.exit(1);
				}
			}
			GlobalScreen.addNativeKeyListener(stopListener);
		}
	}

	class StopMacroListener implements NativeKeyListener {

		public void nativeKeyPressed(NativeKeyEvent e) {
			if (NativeKeyEvent.getKeyText(e.getKeyCode()) == "Q") {
				System.out.println("Quitting application");
				System.exit(1);
			} else {
				enabled = false;
				xDistanceSwiped = searchPatternXSwipeCount;
				yDistanceSwiped = searchPatternYSwipeCount;
				System.out.println("Stopped the macro");
				GlobalScreen.removeNativeKeyListener(stopListener);
			}
		}

		public void nativeKeyTyped(NativeKeyEvent e) {
		}

		public void nativeKeyReleased(NativeKeyEvent e) {
		}
	}

	class RecordPositionByClickListener implements AWTEventListener {

		public void eventDispatched(AWTEvent event) {
			if (findingCoords) {
				Point loc = MouseInfo.getPointerInfo().getLocation();
				double x = loc.getX();
				if (findingWhichCoords) {
					double y = loc.getY();
					yCord = (int) y;
					startXCord = (int) x;
					startCordText.setText("Starting x-axis Coordinate: " + x);
					startYCordText.setText("y-axis positioning: " + y);
				} else {
					endCord = (int) x;
					endCordText.setText("End x-axis Coordinate: " + x);
				}
				if (startXCord > 0 && endCord > 0) {
					searchPatternWidthField.setEnabled(true);
					searchPatternHeightField.setEnabled(true);
					setHeight.setEnabled(true);
					setWidth.setEnabled(true);
					int distance;
					if (startXCord > endCord) {
						distance = startXCord - endCord;
					} else {
						distance = endCord - startXCord;
					}
					xDragDistance.setText("<html><B>" + "X-Drag Distance: " + distance + "</B></html>");
					yDragDistance.setVisible(true);
					xDragDistanceVar = distance;
				}
				detach();
			}
		}
	}

	class SetSearchWidthValue implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String value = searchPatternWidthField.getText();
			searchPatternWidthValueLabel.setText("Search Width: " + value);
			searchPatternXSwipeCount = Integer.parseInt(value);
		}
	}

	class SetSearchHeightValue implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String value = searchPatternHeightField.getText();
			searchPatternHeightValueLabel.setText("Search Height: " + value);
			searchPatternYSwipeCount = Integer.parseInt(value);
		}
	}

	class IntFilter extends DocumentFilter {
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			} else {
				// warn the user and don't allow the insert
			}
		}

		private boolean test(String text) {
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			} else {
				// warn the user and don't allow the insert
			}

		}

		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);
			if (sb.toString().length() == 0) {
				super.replace(fb, offset, length, "", null);
			} else {
				if (test(sb.toString())) {
					super.remove(fb, offset, length);
				} else {
					// warn the user and don't allow the insert } }
				}
			}
		}
	}
}
