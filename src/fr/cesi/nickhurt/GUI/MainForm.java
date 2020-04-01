package fr.cesi.nickhurt.GUI;

import com.sun.deploy.util.ArrayUtil;
import sun.misc.IOUtils;
import sun.misc.Regexp;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class MainForm extends JFrame{
    private JPanel mainPanel;
    private JPanel filePanel;
    private DropTarget dropTarget;
    private DropTargetHandler dropTargetHandler;
    private JLabel keyLabel;
    private JPanel inputPanel;
    private JTextField keyField;
    private JButton DecypherButton;
    private JButton explorerButton;
    private JFileChooser fc;
    private String filePath;
    private byte[] fileContent;

    public MainForm() {
        //filePanel = new FilePanel();
        JFrame frame = new JFrame("Déchiffrator3000");
        frame.setContentPane(mainPanel);
        mainPanel.setLayout(new GridLayout(10,1));
        //mainPanel.add(filePanel);
        frame.setPreferredSize(new Dimension(500,500));
        filePanel.setLayout(new GridBagLayout());
        filePanel.setPreferredSize( new Dimension(200,200));
        filePanel.setBorder(new LineBorder(Color.BLACK));
        filePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("clicked");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        DecypherButton.addActionListener(e -> {
            System.out.println("open explorer");
            String decodedMessage = decodeByte(keyField.getText(), fileContent);
            write(decodedMessage);
            //generateKeys(6);
            //compareToDictionary(decodedMessage);
        });

        explorerButton.addActionListener(e -> {
            fc = new JFileChooser();
            fc.setCurrentDirectory(new File("E:/Users/Nico/Desktop/projet crypto/FICHIERS CRYTES"));
            fc.setDialogTitle("Select encrypted file");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            fc.showOpenDialog(explorerButton);
            filePath = fc.getSelectedFile().getAbsolutePath();
            fileContent = readFileByte(filePath);
            System.out.println("displayed bytes");
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private DropTarget getMyDropTarget() {
        if (dropTarget == null) {
            dropTarget = new DropTarget(this, DnDConstants.ACTION_LINK, null);
        }
        return dropTarget;
    }

    private DropTargetHandler getDropTargetHandler() {
        if (dropTargetHandler == null) {
            dropTargetHandler = new DropTargetHandler();
        }
        return dropTargetHandler;
    }

    public byte[] readFileByte(String path) {
        File file = new File(path);
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            System.out.println(Arrays.toString(fileContent));
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Integer> readFile(String filePath) {
        final StringBuilder filecontent = new StringBuilder();
        ArrayList<Integer> array = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis, "Cp1252");

            System.out.println(isr.getEncoding());
            BufferedReader reader = new BufferedReader(isr);
            String line;
            int c;
            while((c = reader.read()) != -1)         //Read char by Char
            {
                array.add(c);
                char character = (char) c;          //converting integer to char
                filecontent.append(character);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return array;
    }

    public String decodeByte(String key, byte[] message) {
        String longKey = "";

        int nbRepete = message.length/key.length();
        int leftover = message.length%key.length();

        for (int i = 0; i< nbRepete; i++){
            longKey += key;
        }
        longKey += key.substring(0,leftover);
        String xorString = "";

        for(int j =0; j < message.length; j++) {
            byte codedChar = message[j];
            byte keyChar = (byte)longKey.charAt(j);
            int keyCharInt = longKey.charAt(j);
            if(codedChar < 0) {
                System.out.println(codedChar);
                int positive = codedChar*-1;
                int unsigned = positive+127;
                System.out.println(positive);
                System.out.println(unsigned);
                int codedCharInt = (codedChar+256);
                int xoredCharInt = codedCharInt^keyCharInt;
                System.out.println(xoredCharInt+" "+(char)xoredCharInt);
                xorString+=(char)xoredCharInt;
            } else {
                byte xoredChar =(byte) (codedChar ^keyChar);
                xorString+=(char)xoredChar;
            }

        }
        System.out.println(xorString);
        return xorString;
    }

    public String decode(String key, ArrayList<Byte> message) {

        System.out.println(key);
        int nbRepete = message.size()/key.length();
        int leftover = message.size()%key.length();
        System.out.println("keysize");
        System.out.println(nbRepete);

        String longKey = "";

        for (int i = 0; i< nbRepete; i++){
            longKey += key;
        }
        longKey += key.substring(0,leftover);

        System.out.println(message.size());
        System.out.println(longKey.length());

        String xorString = "";

        ArrayList<Integer> xoredArray = new ArrayList<>();
        for(int j =0; j < message.size(); j++) {
            int codedChar = message.get(j);
            char keyChar = longKey.charAt(j);

            if(codedChar > 255 || codedChar < 1){
                System.out.println("overflow");
                System.out.println(codedChar);
            }

            System.out.println("chars : coded : "+(char) codedChar+" "+codedChar+" key: "+ Character.valueOf(keyChar)+ " "+ keyChar);

            int xorInt = codedChar ^ (keyChar);
            xoredArray.add(xorInt);
            char xorChar = (char) xorInt;
            System.out.println("new char");
            System.out.println(xorInt);
            System.out.println(xorChar);


            xorString += xorChar;
        }
        System.out.println("xored array");
        System.out.println(Arrays.toString(xoredArray.toArray()));
        return xorString;
    }

    public void write(String fileContent) {
        System.out.println("gotta print"+fileContent);
        String outputFile = filePath.replace(".txt","Decoded.txt");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(fileContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
