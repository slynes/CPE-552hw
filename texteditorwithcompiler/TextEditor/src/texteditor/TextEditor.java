package texteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.swing.event.DocumentEvent;
import java.io.InputStreamReader;
import java.io.File;

/**
 *
 * @author shanelynes
 * "I pledge my honor that I have abided by the Stevens Honor System
 */
public class TextEditor extends JFrame {
    private String fileName;
    private String filePath;
    private String teText = "";
        
    public TextEditor(LinkedHashMap<String, ArrayList<String>> menuOptions, Font f) {
        super("New Doc");

        JTextArea t = new JTextArea();
        t.addKeyListener(new TextAreaListener(this, t));

        Container c = getContentPane();

        JMenuBar menuBar = new JMenuBar();
        t.setFont(f);
        c.add(t, BorderLayout.CENTER);

        menuOptions.forEach((key, val) -> {
            if (val != null) {
                JMenu newMenuItem = new JMenu(key);
                val.forEach((listItem) -> {
                    JMenuItem item = new JMenuItem(listItem);
                    if (listItem == "Open") {
                        item.addActionListener(new OpenListener(t, this));
                    }
                    if (listItem == "Quit") {
                        item.addActionListener(new CloseListener(this, t));
                    }
                    if (listItem == "New") {
                        item.addActionListener(new NewListener(this, t));
                    }
                    if (listItem == "Save") {
                        item.addActionListener(new SaveListener(this, t));
                    }
                    if (listItem == "Compile") {
                        item.addActionListener(new CompileFileListener());
                    }
                    if (listItem == "Run") {
                        item.addActionListener(new RunFileListener());
                    }
                    newMenuItem.add(item);
                });
                menuBar.add(newMenuItem);
            } else {
                menuBar.add(new JMenu(key));
            }
        });

        setJMenuBar(menuBar);
        c.setBackground(Color.BLUE);

        setSize(800,600);
        setVisible(true);

    }
        
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setTeText(String text) {
        this.teText = text;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    
    public static ArrayList<String> createMenuItemList(String... elements) {
        ArrayList<String> list = new ArrayList<String>();
        for (String item : elements) {
            list.add(item);
        }
        return list;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        LinkedHashMap<String, ArrayList<String>> menuOptions = new LinkedHashMap<String, ArrayList<String>>() {
            {
                put("File", createMenuItemList("New", "Save", "Open", "Quit"));
                put("Build", createMenuItemList("Compile", "Run"));
            }
        };
       
        Font f = new Font("Helvetica", Font.BOLD, 24);
       
        new TextEditor(menuOptions, f);
        
    }
    
    class OpenListener implements ActionListener {
        private TextEditor te;
        private JTextArea textArea;
        public OpenListener(JTextArea textArea, TextEditor te) {
            this.textArea = textArea;
            this.te = te;
        }
        
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Java Files", "java");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Open File");
            int returnVal = chooser.showOpenDialog(chooser);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                String fileName = chooser.getSelectedFile().getName();
                try {
                    readFile(filePath);
                    te.setTitle(fileName);
                    te.setFileName(fileName);
                    te.setFilePath(filePath);
                }
                catch (Exception exc) {
                    System.out.println(exc);
                }
            }
        }
        
        public void readFile(String filePath) throws FileNotFoundException {
            String finalText = "";
            Scanner s = new Scanner(new BufferedReader(new FileReader(filePath)));
            while (s.hasNextLine()) {
                finalText += s.nextLine();
                finalText += "\n";
            }
            textArea.setText(finalText);
            te.setTeText(finalText);
        }
        
    }
    
    class CloseListener implements ActionListener {
        private TextEditor te;
        private JTextArea ta;
        public CloseListener(TextEditor te, JTextArea ta) {
            this.te = te;
            this.ta = ta;
        }
        
        public void actionPerformed(ActionEvent e) {
            String title = this.te.getTitle();
            if (title.contains("*")) {
                //default icon, custom title
                int reply = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to save unsaved work?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    if (te.fileName == null) {
                        saveDialog(this.te, this.ta);   
                    } else {
                        try {
                            saveFile(te.filePath, ta.getText());
                            te.setTitle(te.fileName);
                            te.setTeText(ta.getText());
                        }
                        catch (Exception exc) {
                            System.out.println(exc);
                        }
                    }
                }
            }
            System.exit(0);
        }
    }
    
    class NewListener implements ActionListener {
        private TextEditor te;
        private JTextArea jt;
        
        public NewListener(TextEditor te, JTextArea jt) {
            this.te = te;
            this.jt = jt;
        }
        public void actionPerformed(ActionEvent e) {
            String title = this.te.getTitle();
            if (title.contains("*")) {
                //default icon, custom title
                int reply = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to save unsaved work?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    if (te.fileName == null) {
                        saveDialog(this.te, this.jt);   
                    } else {
                        try {
                            saveFile(te.filePath, jt.getText());
                            te.setTitle(te.fileName);
                            te.setTeText(jt.getText());
                        }
                        catch (Exception exc) {
                            System.out.println(exc);
                        }
                    }
                }
                this.jt.setText("");
                te.setTitle("New Doc");
            } else {
                this.jt.setText("");
                te.setTitle("New Doc");
            }
        }
    }
    
    class SaveListener implements ActionListener {
        private TextEditor te;
        private JTextArea ta;
        public SaveListener(TextEditor te, JTextArea ta) {
            this.te = te;
            this.ta = ta;   
        }
        
        public void actionPerformed(ActionEvent e) {
            if (te.fileName == null) {
                saveDialog(this.te, this.ta);   
            } else {
                try {
                    saveFile(te.filePath, ta.getText());
                    te.setTitle(te.fileName);
                    te.setTeText(ta.getText());
                }
                catch (Exception exc) {
                    System.out.println(exc);
                }
            }
        }
    }
    
    public void saveDialog(TextEditor te, JTextArea ta) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Java Files", "java");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Save File");
        int returnVal = chooser.showSaveDialog(chooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            String fileName = chooser.getSelectedFile().getName();
            te.setFileName(fileName);
            try {
                saveFile(path, ta.getText());
                te.setTitle(fileName);
                te.setFilePath(path);
                te.setFileName(fileName);
                te.setTeText(ta.getText());
            }
            catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }
    
    public void saveFile(String filePath, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(text);
        writer.close();
    }
    
    class TextAreaListener implements KeyListener {
        public TextEditor te;
        public JTextArea ta;
        
        public TextAreaListener(TextEditor te, JTextArea ta) {
            this.te = te;
            this.ta = ta;
        }
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            String title = te.getTitle();
            if (!te.teText.equals(ta.getText())) {
                if (!title.contains("*")) {
                    te.setTitle(te.getTitle() + '*');
                }
            } else {
                if (te.fileName != null) {
                    te.setTitle(te.fileName);
                } else {
                    te.setTitle("New Doc");
                }
            }
        }
    }
    
    class CompileFileListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            try {
                int status = compileUsingRunTime(fileName, filePath);
                if (status == 0) {
                    JOptionPane.showMessageDialog(null, "Successfully Compiled!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to compile", "Failure", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }
    
    class RunFileListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                int status = runUsingRunTime(fileName, filePath);
                if (status == 0) {
                    JOptionPane.showMessageDialog(null, "Successfully Ran! Check NetBeans for printed output", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to Run", "Failure", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }
    
    public static int compileUsingRunTime(String fileName, String filePath) throws Exception {
        String fileDirectory = filePath.substring(0, filePath.lastIndexOf("/"));
        File curDir = new File(fileDirectory);
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("javac " + fileName, null, curDir);
        int status = p.waitFor();
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        while ((line = isr.readLine()) != null) {
            System.out.println(line);
        }
        return status;   
    }
    
    public static int runUsingRunTime(String fileName, String filePath) throws Exception {
        String fileDirectory = filePath.substring(0, filePath.lastIndexOf("/"));
        String className = fileName.substring(0, fileName.lastIndexOf("."));
        File curDir = new File(fileDirectory);
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("java " + className, null, curDir);
        int status = p.waitFor();
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        while ((line = isr.readLine()) != null) {
            System.out.println(line);
        }
        if (status == 0) {
            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = output.readLine()) != null) {
                System.out.println(line);
            }
        }
        return status;          
    }

}
    
