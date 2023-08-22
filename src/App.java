import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class App {
   static boolean enabled = false;
   static boolean findingCoords = false;
   static boolean findingWhichCoords = false;
   static int startCord;
   static int endCord;
   static JLabel startCordText;
   static JLabel endCordText;
   static JLabel xDragDistance;
   static JLabel yDragDistance;
   static int xDragDistanceVar;
   static int xDistanceDragged = 0;
   static int yDistanceDragged = 0;
   static int yDragDistanceVar = 200;
   JTextField searchPatternWidthField;
   JTextField searchPatternHeightField;
   JLabel searchPatternWidthValueLabel;
   JLabel searchPatternHeightValueLabel;
   static int searchPatternHeight;
   static int searchPatternWidth;
   static boolean isXDirectionFlipped = false;
   static boolean isYDirectionFlipped = false;
   JButton setWidth;
   JButton setHeight;
   JCheckBox checkRepeat;
   static boolean isRepeating = false;
   RecordPositionByClickListener listener = new RecordPositionByClickListener();

   public void start() {
      setUpGui();
      try {
         Robot r = new Robot();
         int button = InputEvent.BUTTON1_DOWN_MASK;
         int current = 0;
         while (true) {
            if (enabled) {
               while (yDistanceDragged < searchPatternHeight) {
                  while (xDistanceDragged < searchPatternWidth) {
                     if (isXDirectionFlipped) {
                        r.mouseMove(endCord, 560);
                        r.mousePress(button);
                        current = 0;
                        while (current != xDragDistanceVar) {
                           if (startCord > endCord) {
                              r.mouseMove(endCord + current, 560);
                           } else {
                              r.mouseMove(endCord - current, 560);
                           }
                           current++;
                           Thread.sleep(4);
                        }
                        r.mouseRelease(button);
                        xDistanceDragged = xDistanceDragged + xDragDistanceVar;
                     } else {
                        r.mouseMove(startCord, 560);
                        r.mousePress(button);
                        current = 0;
                        while (current != xDragDistanceVar) {
                           if (startCord > endCord) {
                              r.mouseMove(startCord - current, 560);
                           } else {
                              r.mouseMove(startCord + current, 560);
                           }
                           current++;
                           Thread.sleep(4);
                        }
                        r.mouseRelease(button);
                        xDistanceDragged = xDistanceDragged + xDragDistanceVar;
                     }
                     Thread.sleep(500);
                  }
                  if (isYDirectionFlipped) {
                     r.mouseMove(startCord, 560);
                     r.mousePress(button);
                     current = 0;
                     while (current < 200) {
                        r.mouseMove(startCord, 560 + current);
                        current++;
                        Thread.sleep(4);
                     }
                  } else {
                     r.mouseMove(startCord, 760);
                     r.mousePress(button);
                     current = 0;
                     while (current < 200) {
                        r.mouseMove(startCord, (760 - current));
                        current++;
                        Thread.sleep(4);
                     }
                  }
                  r.mouseRelease(button);
                  yDistanceDragged = yDistanceDragged + 200;
                  xDistanceDragged = 0;
                  isXDirectionFlipped = !isXDirectionFlipped;
                  Thread.sleep(400);
               }
               if (isRepeating) {
                  // find where x
                  yDistanceDragged = 0;
                  isYDirectionFlipped = !isYDirectionFlipped;
               } else {
                  enabled = false;
                  xDistanceDragged = 0;
                  yDistanceDragged = 0;
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

      JLabel yLabel = new JLabel("y-axis positioning: 560");
      JLabel searchPatternWidthLabel = new JLabel("Search Pattern Width: ");
      JLabel searchPatternHeightLabel = new JLabel("Search Pattern Height: ");
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

      setWidth = new JButton("Set Width");
      setWidth.addActionListener(new SetSearchWidthValue());
      setWidth.setEnabled(false);
      setHeight = new JButton("Set Height");
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

      innerPanelCenter.add(yLabel);
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
      App app = new App();
      app.start();
   }

   public void detach() {
      Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
      findingCoords = false;
      System.out.println("Detached the Listener!");
   }

   class FindStartCoordListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent event) {
         System.out.println("starting to find start coords");
         findingCoords = true;
         findingWhichCoords = true;
         Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.FOCUS_EVENT_MASK);
      }
   }

   class FindEndCoordListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent event) {
         System.out.println("starting to find end coords");
         findingCoords = true;
         findingWhichCoords = false;
         Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.FOCUS_EVENT_MASK);
      }
   }

   class CheckRepeatListener implements ItemListener {
      @Override
      public void itemStateChanged(ItemEvent ev) {
         if (checkRepeat.isSelected()) {
            isRepeating = true;
         } else {
            isRepeating = false;
         }
      }
   }

   class StartMacroListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if (startCord < 0 || endCord < 0 || searchPatternWidth < xDragDistanceVar
               || searchPatternHeight < yDragDistanceVar) {
            System.out.println("failed to start macro");
            return;
         }
         enabled = !enabled;
         yDistanceDragged = 0;
         xDistanceDragged = 0;
         System.out.println("Started the macro");
         try {
            GlobalScreen.registerNativeHook();
         } catch (NativeHookException ex) {
            System.err.println("There was an error registering the native hook");
            System.err.println(ex.getMessage());

            System.exit(1);
         }
         GlobalScreen.addNativeKeyListener(new StopMacroListener());
      }
   }

   class StopMacroListener implements NativeKeyListener {

      @Override
      public void nativeKeyPressed(NativeKeyEvent e) {
         if (NativeKeyEvent.getKeyText(e.getKeyCode()) == "Q") {
            System.out.println("Quitting application");
            System.exit(1);
         } else {
            enabled = false;
            xDistanceDragged = searchPatternWidth;
            yDistanceDragged = searchPatternHeight;
            System.out.println("Stopped the macro");
         }
      }

      @Override
      public void nativeKeyTyped(NativeKeyEvent e) {
      }

      @Override
      public void nativeKeyReleased(NativeKeyEvent e) {
      }
   }

   class RecordPositionByClickListener implements AWTEventListener {
      @Override
      public void eventDispatched(AWTEvent event) {
         if (findingCoords) {
            Point loc = MouseInfo.getPointerInfo().getLocation();
            double x = loc.getX();
            if (findingWhichCoords) {
               startCord = (int) x;
               startCordText.setText("Starting x-axis Coordinate: " + x);
            } else {
               endCord = (int) x;
               endCordText.setText("End x-axis Coordinate: " + x);
            }
            if (startCord > 0 && endCord > 0) {
               searchPatternWidthField.setEnabled(true);
               searchPatternHeightField.setEnabled(true);
               setHeight.setEnabled(true);
               setWidth.setEnabled(true);
               int distance;
               if (startCord > endCord) {
                  distance = startCord - endCord;
               } else {
                  distance = endCord - startCord;
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
      @Override
      public void actionPerformed(ActionEvent e) {
         String value = searchPatternWidthField.getText();
         searchPatternWidthValueLabel.setText("Search Width: " + value);
         searchPatternWidth = Integer.parseInt(value);
      }
   }

   class SetSearchHeightValue implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         String value = searchPatternHeightField.getText();
         searchPatternHeightValueLabel.setText("Search Height: " + value);
         searchPatternHeight = Integer.parseInt(value);
      }
   }

   class IntFilter extends DocumentFilter {
      public void insertString(FilterBypass fb, int offset, String string,
            AttributeSet attr) throws BadLocationException {

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

      public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {

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

      public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
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
