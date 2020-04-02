package fr.cesi.nickhurt.GUI;

import com.google.common.util.concurrent.*;
import fr.cesi.nickhurt.DAO.DBDAO;
import fr.cesi.nickhurt.DAO.fileDAO;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainForm extends JFrame{
    public String DICTIONARY_PATH = "E:\\Users\\Nico\\Desktop\\projet crypto\\Ressources\\liste_francais.txt";
    public String KEYS_FILEPATH = "E:\\Users\\Nico\\Desktop\\projet crypto\\keys.txt";
    private JPanel mainPanel;
    private JPanel filePanel;
    private DropTarget dropTarget;
    private DropTargetHandler dropTargetHandler;
    private JLabel keyLabel;
    private JTextField keyField;
    private JButton DecypherButton;
    private JButton explorerButton;
    private JLabel noKeyLabel;
    private JButton bruteForceButton;
    private JFileChooser fc;
    private String filePath;
    private byte[] fileContent;
    public int nbOfDictionaryRequests;
    public int nbOfDictionaryComparisons;
    public String[] dictionary;
    public Instant startTime;

    public MainForm() {
        //filePanel = new FilePanel();
        JFrame frame = new JFrame("Dehacking tool");
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
            frequencyPretest(decodedMessage);
            write(decodedMessage);
            if (testFileCoherence(decodedMessage)) {
                System.out.println("File deciphered with success");
            }
        });

        bruteForceButton.addActionListener(e -> {
            //testDecodeTime();
//            String[] keys = generateKeys(6);

            bruteForce();

//            for(String key: keys) {
//                String decodedMessage = decodeByte(key, fileContent);
//                if (testFileCoherence(decodedMessage)) {
//                    System.out.println("File deciphered with success");
//                    write(decodedMessage);
//                } else {
//                    System.out.println("wrong key: "+key);
//                }
//            }
        });

        explorerButton.addActionListener(e -> {
            fc = new JFileChooser();
            fc.setCurrentDirectory(new File("E:/Users/Nico/Desktop/projet crypto/FICHIERS CRYTES"));
            fc.setDialogTitle("Select encrypted file");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            fc.showOpenDialog(explorerButton);
            filePath = fc.getSelectedFile().getAbsolutePath();
//            fileContent = readFileByte(filePath);
            fileContent = fileDAO.getInstance().getFileContent(filePath);
            System.out.println("displayed bytes");
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void testDecodeTime() {
        startTime = Instant.now();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < getNumberOfKeys(6); i++) {
            String key = getKey(i, alphabet);
            String decodedMessage = decodeByte(key, Arrays.copyOfRange(fileContent, 0, 100));
            if(i%10000 ==0) {
                System.out.println("tried "+i+" keys");
                Instant currentTime = Instant.now();
                Duration timeElapsed = Duration.between(startTime, currentTime);
                System.out.println("Time taken: "+ timeElapsed.toMillis()/1000 +" seconds");
            }
        }
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
            //System.out.println(Arrays.toString(fileContent));
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
                //System.out.println(codedChar);
                int positive = codedChar*-1;
                int unsigned = positive+127;
                //System.out.println(positive);
                //System.out.println(unsigned);
                int codedCharInt = (codedChar+256);
                int xoredCharInt = codedCharInt^keyCharInt;
                //System.out.println(xoredCharInt+" "+(char)xoredCharInt);
                xorString+=(char)xoredCharInt;
            } else {
                byte xoredChar =(byte) (codedChar ^keyChar);
                xorString+=(char)xoredChar;
            }

        }
        //System.out.println(xorString);
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
        //System.out.println("gotta print"+fileContent);
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

    public ListenableFuture<String> bruteForceSubprocess(ListeningExecutorService service, int startIndex, int nbOfThreads) {
        ListenableFuture<String> future = service.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                for(int i = startIndex; i< getNumberOfKeys(6); i+=nbOfThreads) {
                    if (Thread.currentThread().isInterrupted()) { break; }
                    String alphabet = "abcdefghijklmnopqrstuvwxyz";
                    String key = getKey(i, alphabet);
                    //System.out.println(key);
                    String decodedSubMessage = decodeByte(key, Arrays.copyOfRange(fileContent, 0, 150));
                    if(i%1000000 ==690159) {
                        notifyProgress(i);
                    }

                    if(!frequencyPretest(decodedSubMessage)) {
                        continue;
                    }
                    String decodedMessage = decodedSubMessage;
                    if (testFileCoherence(decodedMessage)) {
                        notifyProgress(i);
                        write(decodedMessage);
                        return key;
                    }
                }
                throw new Exception("Subprocess "+startIndex+" finished without finding key");
            }
        });
        return future;
    }

    public void bruteForce() {
        startTime = Instant.now();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4));
        ArrayList<ListenableFuture<String>> futureList = new ArrayList<>();
        int nbOfThreads = 10;
        for(int i = 0; i < nbOfThreads; i++){
            ListenableFuture<String> subProcess = bruteForceSubprocess(service, i, nbOfThreads);
            futureList.add(subProcess);
            Futures.addCallback(subProcess, new FutureCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("key given by thread : "+s);
                    System.out.println("killing any remaining threads");
                    service.shutdown();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("thread failed or finished without success");
                    System.out.println(throwable.getMessage());
                }
            });
        }

//        startTime = Instant.now();
////        DBDAO.getInstance().populateDatabase(getDictionary());
//        String alphabet = "abcdefghijklmnopqrstuvwxyz";
//            for(int i = 0; i< getNumberOfKeys(6); i++) {
////        for(int i = 0; i< getNumberOfKeys(6); i++) {
//            String key = getKey(i, alphabet);
//            //System.out.println(key);
//            String decodedSubMessage = decodeByte(key, Arrays.copyOfRange(fileContent, 0, 150));
//            if(i%500000 ==0) {
//                notifyProgress(i);
//            }
//
//            if(!frequencyPretest(decodedSubMessage)) {
////                    System.out.println("continue");
//                continue;
//            }
//
//            //String decodedMessage = decodeByte(key, fileContent);
//            String decodedMessage = decodedSubMessage;
//            if (testFileCoherence(decodedMessage)) {
//                System.out.println("File deciphered with success. key :"+key);
//                notifyProgress(i);
//                write(decodedMessage);
//                break;
//            } else {
//                if(i%1000 ==0) {
//                    System.out.println("wrong key: "+key);
//                    System.out.println("tried "+i+" keys");
//                    System.out.println(nbOfDictionaryRequests+" dictionary requests");
//                    System.out.println(nbOfDictionaryComparisons +" dictionary comparisons");
//                }
//                if(i%10000 == 0) {
//                    Instant currentTime = Instant.now();
//                    Duration timeElapsed = Duration.between(startTime, currentTime);
//                    System.out.println("Time taken: "+ timeElapsed.toMillis()/1000 +" seconds");
//                }
//            }
//        }
    }

    public void notifyProgress(int keyNumber) {
        System.out.println("tried "+keyNumber+" keys");
        Instant currentTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, currentTime);
        System.out.println(nbOfDictionaryRequests+" dictionary requests");
        System.out.println(nbOfDictionaryComparisons +" dictionary comparisons");
        System.out.println("Time taken: "+ timeElapsed.toMillis()/1000 +" seconds");
    }

    public String getKey(int i, String alphabet) {
        int alphabetSize = alphabet.length();
        return i < 0 ? "" : getKey((i / alphabetSize)-1, alphabet) + alphabet.toCharArray()[i % 26];
    }

    public int getNumberOfKeys(int maxKeyLength) {
        int numberOfKeys = 0;
        for(int i =1; i<= maxKeyLength; i++) {
            numberOfKeys += Math.pow(26,i);
        }
        return  numberOfKeys;
    }

    public String[] generateKeys(int maxKeyLength) {
        int numberOfKeys = 0;
        for(int i =1; i<= maxKeyLength; i++) {
            numberOfKeys += Math.pow(26,i);
        }
        System.out.println(numberOfKeys+" possible keys");
        String[] keys = new String[numberOfKeys];
        String fileName = KEYS_FILEPATH;
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        try (FileOutputStream fos = new FileOutputStream(fileName, true)) {

        for(int i =0; i< numberOfKeys; i++) {
            String key = getKey(i, alphabet);
            keys[i] = key;
            System.out.println(keys[i]);
            fos.write((key+"\n").getBytes());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keys;
    }

    public boolean testFileCoherence(String decodedMessage) {
        return testFileCoherence(decodedMessage, null);
    }

    public boolean testFileCoherence(String decodedMessage, String[] dictionaryWords) {
//        String sample = decodedMessage.substring(0,300);

        int invalidChars = 0;
        int totalChars = 0;
        int invalidWords = 0;
        int validWords = 0;
        int totalWords = 0;
        int matchedWords = 0;
        int unmatchedWords = 0;

        String[] words = decodedMessage.split(" |\\.|,|'|\\(|\\)|\\n|\\r");
        if(words.length < 6) {
//            System.out.println("less than 5 words in 100 first chars, unlikely");
            return false;
        }
        for(String word : words) {
            int invalidCharInWord = 0;
            boolean isInvalid = false;
            for(char letter: word.toCharArray()) {
                totalChars++;
                if((int) letter > 255 || (int) letter < 0 || !(""+letter).matches("[A-zÀ-ú]*[?\\-]*")) {
                    //System.out.println("invalid char "+letter+" in word "+ word);
                    invalidCharInWord++;
                    invalidChars++;
                }
                if(invalidCharInWord > 1) {
                    //System.out.println("2 invalid char in word "+word+" , breaking");
                    invalidWords++;
                    isInvalid = true;
                    break;
                }

            }
            if(invalidWords > totalWords/10 && invalidChars > 9) { //could see the frequency of é,è,ç,à in french to establish the right percentage
                //System.out.println(invalidWords+"/"+totalWords);
                //System.out.println("over 10% invalid words, wrong key, return");
                return false;
            } else {
                if(!isInvalid && word.length() > 3) {
                    validWords++;
                    //System.out.println("will compare with dictionary :"+ word);
                    boolean matched = compareToDictionary(word);
//                    nbOfDictionaryRequests = fileDAO.getInstance().getNbOfDictionaryRequests();
//                    nbOfDictionaryComparisons= fileDAO.getInstance().getNbOfDictionaryComparisons();
                    nbOfDictionaryRequests = DBDAO.getInstance().getNbOfDictionaryRequests();
                    nbOfDictionaryComparisons= DBDAO.getInstance().getNbOfDictionaryComparisons();
//                    boolean matched = compareToTextFile(word);
                    if(matched) matchedWords++; else unmatchedWords++;
                    //System.out.println(matched+" matched = "+matchedWords+" unmatched= "+unmatchedWords);


                    if(validWords > 9 && (float)matchedWords/validWords < 0.3) {
                        //System.out.println("didn't match enough words, key discarded");
                        return false;
                    } else if (validWords > 9 && (float)matchedWords/validWords > 0.5){
                        System.out.println("over 50% of the words matched, found the key !");
                        return true;
                    }
                    //[A-zÀ-ú]*
                }
            }
        }
        //System.out.println("done: "+totalChars+" chars, "+invalidChars+" invalid chars ("+(float)invalidChars/totalChars+"%), "+invalidWords+" invalid words ("+(float)invalidWords/words.length+"%), "+matchedWords+" matched");
        //System.out.println("Could not conclude on file validity.");
        return false;
    }

    public boolean frequencyPretest(String message) {

//        String testSubset = message.substring(0,150);
//        String[] words = testSubset.split(" |\\.|,|'|\\(|\\)|\\n|\\r");
        String[] words = message.split(" |\\.|,|'|\\(|\\)|\\n|\\r");
        Map<Integer, Long> letterCount = countLetters(words);

        if(!(letterCount.containsKey(101) && letterCount.containsKey(115) && letterCount.containsKey(97))) {
            return false;
        }

        long numberOfE = letterCount.get(101);
        long numberOfS = letterCount.get(115);
        long numberOfA = letterCount.get(97);

        float percentageE = (float)numberOfE/message.length(); // 14.7% in french
        float percentageS = (float)numberOfS/message.length(); // 7.9% in french
        float percentageA = (float)numberOfA/message.length(); // 7.6% in french
        float totalPercentage = percentageE+ percentageS+ percentageA; // 30.2% in french

        if(totalPercentage<0.2) { //50% uncertainty should be plenty to avoid false negative
            return false;
        } else {
            //System.out.println("OVER 20%"+totalPercentage);
            return true;
        }
    }

    public static Map<Integer, Long> countLetters(String[] words) {
        return Arrays.stream(words)
                .flatMapToInt(String::chars)
                .filter(Character::isLetter)
                .boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public boolean compareToDictionary(String wordToCompare) {
//        return compareToTextFile(wordToCompare);
//        return fileDAO.getInstance().checkWord(wordToCompare);
        return DBDAO.getInstance().checkWord(wordToCompare);
    }

    public boolean compareToDatabase(String wordToCompare) {
        return DBDAO.getInstance().checkWord(wordToCompare);
    }

    //TODO implement a DAO
    public boolean compareToTextFile(String wordToCompare) {
        String[] dictionary = getDictionary();
        nbOfDictionaryRequests++;
        for( String dictionaryWord: dictionary) {
            nbOfDictionaryComparisons++;
            String escapedWord = Pattern.quote(dictionaryWord);
            //System.out.println(escapedWord);
            if(wordToCompare.matches(escapedWord)){
                //System.out.println(wordToCompare+" matched with dictionary word "+dictionaryWord);
                return true;
            } else {
                //System.out.println(wordToCompare+" didn't match with dictionary word "+dictionaryWord);
            }
        }
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
//        if(dictionary == null) {
            ArrayList<String> dictionaryList = new ArrayList<>();
            try {
                FileInputStream inputFile = new FileInputStream(DICTIONARY_PATH);
                InputStreamReader ir = new InputStreamReader(inputFile, "Cp1252");
                BufferedReader br = new BufferedReader(ir);  //creates a buffering character input stream
                String line;
                while ((line = br.readLine()) != null) {
                    dictionaryList.add(line);
                }
                String[] dictionaryArray = new String[dictionaryList.size()];
                dictionary = dictionaryList.toArray(dictionaryArray);
                return dictionary;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
//        } else {
//            return dictionary;
//        }
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
