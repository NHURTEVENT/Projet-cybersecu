package fr.cesi.nickhurt.GUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MainForm extends JFrame{
    public String DICTIONARY_PATH = "E:\\Users\\Nico\\Desktop\\projet crypto\\Ressources\\liste_francais.txt";
    private JPanel mainPanel;
    private JPanel filePanel;
    private DropTarget dropTarget;
    private DropTargetHandler dropTargetHandler;
    private JLabel keyLabel;
    private JPanel inputPanel;
    private JTextField keyField;
    private JButton DecypherButton;
    private JButton explorerButton;
    private JLabel noKeyLAbel;
    private JButton bruteForceButton;
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
            if (testFileCoherence(decodedMessage)) {
                System.out.println("File deciphered with success");
            }
        });

        bruteForceButton.addActionListener(e -> {
            String[] keys = generateKeys(6);
            for(String key: keys) {

            }
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
    public void bruteForce(ArrayList<Integer> encodedMessage) {

    }

    public String[] generateKeys(int maxKeyLength) {
        int numberOfKeys = 0;
        for(int i =1; i<= maxKeyLength; i++) {
            numberOfKeys += Math.pow(26,i);
        }
        System.out.println(numberOfKeys+" possible keys");
        String[] keys = new String[numberOfKeys];
        return keys;
    }

    public boolean testFileCoherence(String decodedMessage) {
        return testFileCoherence(decodedMessage, null);
    }

    public boolean testFileCoherence(String decodedMessage, String[] dictionaryWords) {
//        String sample = decodedMessage.substring(0,300);
        String[] words = decodedMessage.split(" |\\.|,|'|\\(|\\)|\\n|\\r");
        int invalidChars = 0;
        int totalChars = 0;
        int invalidWords = 0;
        int validWords = 0;
        int totalWords = 0;
        int matchedWords = 0;
        int unmatchedWords = 0;
        for(String word : words) {
            int invalidCharInWord = 0;
            boolean isInvalid = false;
            for(char letter: word.toCharArray()) {
                totalChars++;
                if((int) letter > 255 || (int) letter < 0 || !(""+letter).matches("[A-zÀ-ú]*[?\\-]*")) {
                    System.out.println("invalid char "+letter+" in word "+ word);
                    invalidCharInWord++;
                    invalidChars++;
                }
                if(invalidCharInWord > 1) {
                    System.out.println("2 invalid char in word "+word+" , breaking");
                    invalidWords++;
                    isInvalid = true;
                    break;
                }
            }
            if(invalidWords > totalWords/10 && invalidChars > 9) { //could see the frequency of é,è,ç,à in french to establish the right percentage
                System.out.println(invalidWords+"/"+totalWords);
                System.out.println("over 10% invalid words, wrong key, return");
                return false;
            } else {
                if(!isInvalid && word.length() > 3) {
                    validWords++;
                    System.out.println("will compare with dictionary :"+ word);
                    boolean matched = compareToDictionary(word);
                    if(matched) matchedWords++; else unmatchedWords++;
                    System.out.println(matched+" matched = "+matchedWords+" unmatched= "+unmatchedWords);


                    if(validWords > 10 && (float)matchedWords/validWords < 0.3) {
                        System.out.println("didn't match enough words, key discarded");
                        return false;
                    } else if (validWords > 10 && (float)matchedWords/validWords > 0.5){
                        System.out.println("over 50% of the words matched, found the key !");
                        return true;
                    }
                    //[A-zÀ-ú]*
                }
            }
        }
        System.out.println("done: "+totalChars+" chars, "+invalidChars+" invalid chars ("+(float)invalidChars/totalChars+"%), "+invalidWords+" invalid words ("+(float)invalidWords/words.length+"%)");
        return true;
    }

    public boolean compareToDictionary(String wordToCompare) {
        String[] dictionary = getDictionary();
        for( String dictionaryWord: dictionary) {
            String escapedWord = Pattern.quote(dictionaryWord);
            System.out.println(escapedWord);
            if(wordToCompare.matches(escapedWord)){
                System.out.println(wordToCompare+" matched with dictionary word "+dictionaryWord);
                return true;
            } else {
                System.out.println(wordToCompare+" didn't match with dictionary word "+dictionaryWord);
            }
        }
        return false;
    }

    //TODO implement a DAO
    public boolean compareToTextFile(String wordToCompare) {
        return false;
    }

    public int compareDictionaryToFile(String[] wordsToCompare) {
        for(String word: wordsToCompare) {
            for(String dictionaryWord: getDictionary()) {
//                if(str.indexOf("word") != -1){
//                    System.out.println("EXISTS");
//                }
            }
        }
        return 0;
    }

    public String[] getDictionary() {
        ArrayList<String> dictionary = new ArrayList<>();
        try {
            FileReader fr= new FileReader(DICTIONARY_PATH);   //reads the file
            BufferedReader br= new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb= new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while((line=br.readLine())!=null) {
//                Scanner txtscan = new Scanner(new File(DICTIONARY_PATH));
//            while(txtscan.hasNextLine()){
//                String str = txtscan.nextLine();
//                dictionary.add(str);
                dictionary.add(line);
            }
            String[] dictionaryArray = new String[dictionary.size()];
            return dictionary.toArray(dictionaryArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void test(String message) {
        System.out.println("test");
        for (int i = 0; i < 10; i++) {
            char myChar = message.charAt(i);
            System.out.println(myChar);
            System.out.println((int) myChar);
            System.out.println(Character.valueOf(myChar));
        }
    }
}
