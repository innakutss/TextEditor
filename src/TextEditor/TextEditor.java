package TextEditor;

import javax.swing.*;
import javax.swing.undo.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.Hashtable;

class UndoableTextArea extends TextArea implements StateEditable {

    private final static String KEY_STATE = "UndoableTextAreaKey";
    private boolean textChanged = false;
    private UndoManager undoManager;
    private StateEdit currentEdit;


    public UndoableTextArea(int rows, int columns) {
        super(rows, columns);
        initUndoable();
    }


    public boolean undo() {
        try {
            undoManager.undo();
            return true;
        } catch (CannotUndoException e) {
            System.out.println("Cannot undo");
            return false;
        }
    }

    public boolean redo() {
        try {
            undoManager.redo();
            return true;
        } catch (CannotRedoException e) {
            System.out.println("Cannot redo");
            return false;
        }
    }


    public void storeState(Hashtable state) {
        state.put(KEY_STATE, getText());
    }

    public void restoreState(Hashtable state) {
        Object data = state.get(KEY_STATE);
        if (data != null) {
            setText((String) data);
        }
    }


    private void takeSnapshot() {
        if (textChanged) {
            currentEdit.end();
            undoManager.addEdit(currentEdit);
            textChanged = false;
            currentEdit = new StateEdit(this);
        }
    }

    private void initUndoable() {
        undoManager = new UndoManager();
        currentEdit = new StateEdit(this);

        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent event) {
                if (event.isActionKey()) {
                    takeSnapshot();
                }
            }
        });
        addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent focusEvent) {
                takeSnapshot();
            }
        });
        addTextListener(new TextListener() {
            public void textValueChanged(TextEvent e) {
                textChanged = true;
            }
        });
    }
}



public class TextEditor extends Frame {

    boolean b = true;
    Font font;
    int style = Font.PLAIN;
    int fontSize = 12;
    UndoableTextArea text;
    String fileName, st, fn = "untitled", dn;
    Clipboard clip = getToolkit().getSystemClipboard();
    TextEditor() {

        font = new Font("Courier", style, fontSize);
        setLayout(new GridLayout(1, 1));
        text = new UndoableTextArea(85, 25);

        text.setFont(font);
        add(text);
        MenuBar menuBar = new MenuBar();
        Menu fontType = new Menu("FontType");
        MenuItem one, two, three, four, five, six;
        one = new MenuItem("Times New Roman");
        two = new MenuItem("Helvetica");
        three = new MenuItem("Courier");
        four = new MenuItem("Arial");
        five = new MenuItem("Arial Black");
        six = new MenuItem("Century");

        fontType.add(one);
        fontType.add(two);
        fontType.add(three);
        fontType.add(four);
        fontType.add(five);
        fontType.add(six);

        one.addActionListener(new Type());
        two.addActionListener(new Type());
        three.addActionListener(new Type());
        four.addActionListener(new Type());
        five.addActionListener(new Type());
        six.addActionListener(new Type());

        Menu fontMenu = new Menu("Font");
        MenuItem boldMenu = new MenuItem("Bold");
        MenuItem plainMenu = new MenuItem("Plain");
        MenuItem italicMenu = new MenuItem("Italic");

        fontMenu.add(boldMenu);
        fontMenu.add(plainMenu);
        fontMenu.add(italicMenu);

        boldMenu.addActionListener(new FM());
        plainMenu.addActionListener(new FM());
        italicMenu.addActionListener(new FM());

        Menu size = new Menu("Size");
        MenuItem s1, s2, s3, s4, s5, s6, s7, s8, s9, s10;
        s1 = new MenuItem("10");
        s2 = new MenuItem("12");
        s3 = new MenuItem("14");
        s4 = new MenuItem("16");
        s5 = new MenuItem("18");
        s6 = new MenuItem("20");
        s7 = new MenuItem("22");
        s8 = new MenuItem("24");
        s9 = new MenuItem("26");
        s10 = new MenuItem("28");

        size.add(s1);
        size.add(s2);
        size.add(s3);
        size.add(s4);
        size.add(s5);
        size.add(s6);
        size.add(s7);
        size.add(s8);
        size.add(s9);
        size.add(s10);

        s1.addActionListener(new Size());
        s2.addActionListener(new Size());
        s3.addActionListener(new Size());
        s4.addActionListener(new Size());
        s5.addActionListener(new Size());
        s6.addActionListener(new Size());
        s7.addActionListener(new Size());
        s8.addActionListener(new Size());
        s9.addActionListener(new Size());
        s10.addActionListener(new Size());

        size.addActionListener(new FM());
        fontMenu.add(size);

        Menu file = new Menu("File");
        MenuItem n = new MenuItem("New", new MenuShortcut(KeyEvent.VK_N));
        MenuItem o = new MenuItem("Open", new MenuShortcut(KeyEvent.VK_O));
        MenuItem s = new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S));
        MenuItem e = new MenuItem("Exit", new MenuShortcut(KeyEvent.VK_E));

        n.addActionListener(new New());
        file.add(n);
        o.addActionListener(new Open());
        file.add(o);
        s.addActionListener(new Save());
        file.add(s);
        e.addActionListener(new Exit());
        file.add(e);

        menuBar.add(file);
        addWindowListener(new Window());

        Menu edit = new Menu("Edit");
        MenuItem cut = new MenuItem("Cut", new MenuShortcut(KeyEvent.VK_X));
        MenuItem copy = new MenuItem("Copy", new MenuShortcut(KeyEvent.VK_C));
        MenuItem paste = new MenuItem("Paste",new MenuShortcut(KeyEvent.VK_V));
            cut.addActionListener(new Cut());
            edit.add(cut);
            copy.addActionListener(new Copy());
            edit.add(copy);
            paste.addActionListener(new Paste());
            edit.add(paste);

            Menu color = new Menu("Color");
            MenuItem background = new MenuItem("Background");
            MenuItem foreground = new MenuItem("Foreground");
            background.addActionListener(new BC());
            foreground.addActionListener(new BC());

            Menu undoRedo = new Menu("Undo&Redo");
            MenuItem undo = new MenuItem("Undo");
            MenuItem redo = new MenuItem("Redo");
            undo.addActionListener(new WW());
            redo.addActionListener(new WW());
            undoRedo.add(undo);
            undoRedo.add(redo);
                color.add(background);
                color.add(foreground);

                menuBar.add(edit);
                menuBar.add(fontMenu);
                menuBar.add(fontType);
                menuBar.add(color);
                menuBar.add(undoRedo);

                setMenuBar(menuBar);

                myListener myList = new myListener();
                addWindowListener(myList);
    }


    class WW implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            String se = ae.getActionCommand();

            if (se.equals("Undo"))
                text.undo();
        }
    }


    class myListener extends WindowAdapter {
        public void windowClosing(WindowEvent we) {

            if(!b) {
                System.exit(0);
            }
        }
    }


    class New implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            text.setText(" ");
            setTitle(fileName);
            fileName = "Untitled";
        }
    }


    class Open implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            FileDialog fileDialog = new FileDialog(TextEditor.this, "Select");
            fileDialog.show();
            if ((fileName = fileDialog.getFile()) != null) {
                fileName = fileDialog.getDirectory() + fileDialog.getFile();
                dn = fileDialog.getDirectory();
                setTitle(fileName);
                readFile();
            }
            text.requestFocus();
        }
    }


    class Save implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            FileDialog fileDialog = new FileDialog(TextEditor.this, "SaveFile", FileDialog.SAVE);
            fileDialog.setFile(fileName);
            fileDialog.setDirectory(dn);
            fileDialog.show();

            if (fileDialog.getFile()!= null) {
                fileName = fileDialog.getDirectory() + fileDialog.getFile();
                setTitle(fileName);
                writeFile();
                text.requestFocus();
                }
            }
        }



    class Exit implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            System.exit(0);
        }
    }
          void readFile() {

              BufferedReader bufferedReader;
              StringBuffer stringBuffer = new StringBuffer();
              try {
                  bufferedReader = new BufferedReader(new FileReader(fileName));
                  String line;
                  while ((line = bufferedReader.readLine())!= null)
                  stringBuffer.append(line + "");
                  text.setText(stringBuffer.toString());
                  bufferedReader.close();
              } catch (FileNotFoundException e) {
                  System.out.println("File not found");
              } catch (IOException e) {}
    }

    public void writeFile() {

        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
            String line = text.getText();
            BufferedReader bufferedReader = new BufferedReader(new StringReader(line));

            while ((line = bufferedReader.readLine())!= null) {
                dataOutputStream.writeBytes(line + "");
            }
            dataOutputStream.close();
        } catch (Exception e) {
            System.out.println("File not found");
        }
    }



    class Cut implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            String select = text.getSelectedText();
            StringSelection stringSelection = new StringSelection(select);
            clip.setContents(stringSelection, stringSelection);
            text.replaceRange("", text.getSelectionStart(), text.getSelectionEnd());
        }
    }



    class Copy implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            String select = text.getSelectedText();
            StringSelection clipString = new StringSelection(select);
            clip.setContents(clipString, clipString);
        }
    }



    class Paste implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            Transferable clipTran = clip.getContents(TextEditor.this);
             try {
                 String select = (String)clipTran.getTransferData(DataFlavor.stringFlavor);
                 text.replaceRange(select, text.getSelectionStart(), text.getSelectionEnd());
             } catch (Exception e) {
                 System.out.println("Not starting flavour");
             }
        }
    }



    class Window extends WindowAdapter {

        public void windowClosing(WindowEvent we) {

            if (b) {
                System.exit(0);
            }
        }
    }



    class Size implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            int style = font.getStyle();
            String size = ae.getActionCommand();

            if (size == "10") {
                font = new Font("Courier", style, 10);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "12") {
                font = new Font("Courier", style, 12);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "14") {
                font = new Font("Courier", style, 14);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "16") {
                font = new Font("Courier", style, 16);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "18") {
                font = new Font("Courier", style, 18);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "20") {
                font = new Font("Courier", style, 20);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "22") {
                font = new Font("Courier", style, 22);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "24") {
                font = new Font("Courier", style, 24);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "26") {
                font = new Font("Courier", style, 26);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }

            if (size == "28") {
                font = new Font("Courier", style, 28);
                text.setFont(font);
                fontSize = font.getSize();
                repaint();
            }
        }
    }


    class FM extends Applet implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            String b = ae.getActionCommand();

            if (b == "Bold") {
                font = new Font("Courier", Font.BOLD, fontSize);
                style = font.getStyle();
                text.setFont(font);
            }

            if (b == "Plain") {
                font = new Font("Courier", Font.PLAIN, fontSize);
                style = font.getStyle();
                text.setFont(font);
            }

            if (b == "Italic") {
                font = new Font("Courier", Font.ITALIC, fontSize);
                style = font.getStyle();
                text.setFont(font);
            }
            repaint();
        }
    }


    class Type implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            String fontStyle = ae.getActionCommand();

            if (fontStyle == "Times New Roman") {
                font = new Font("Times New Roman", style, fontSize);
                text.setFont(font);
            }
            if (fontStyle == "Courier") {
                font = new Font("Courier", style, fontSize);
                text.setFont(font);
            }
            if (fontStyle == "Helvetica") {
                font = new Font("Helvetica", style, fontSize);
                text.setFont(font);
            }
            if (fontStyle == "Arial") {
                font = new Font("Arial", style, fontSize);
                text.setFont(font);
            }
            if (fontStyle == "Arial Black") {
                font = new Font("Arial Black", style, fontSize);
                text.setFont(font);
            }
            if (fontStyle == "Century") {
                font = new Font("Century", style, fontSize);
                text.setFont(font);
            }
            repaint();
        }
    }


    class BC implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            st = ae.getActionCommand();
            colorChooser colorChooser = new colorChooser();
            colorChooser.setSize(400, 300);
            colorChooser.setVisible(true);
        }
    }


    class colorChooser extends JFrame {


        Button ok;
        JColorChooser jColorChooser;

        public colorChooser() {
            setTitle("JColorChooser");
            jColorChooser = new JColorChooser();
            JPanel content = (JPanel)getContentPane();
            content.setLayout(new BorderLayout());
            content.add(jColorChooser, "Center");
            ok = new Button("OK");
            content.add(ok, "South");
            ok.addActionListener(new Background());
        }


        class Background implements ActionListener {

            public void actionPerformed(ActionEvent ae) {
                System.out.println("Color is: " + jColorChooser.getColor().toString());
                if (st.equals("Background"))
                    text.setBackground(jColorChooser.getColor());
                if (st.equals("Foreground"))
                    text.setForeground(jColorChooser.getColor());
                setVisible(false);
            }
        }
    }

    public static void main(String[] args) {

        Frame frame = new TextEditor();
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.show();
    }
    }