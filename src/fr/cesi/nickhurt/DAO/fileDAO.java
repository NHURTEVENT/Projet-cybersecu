package fr.cesi.nickhurt.DAO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class fileDAO implements iDAO {
    private static iDAO myDAO = null;
    private final String FILES_PATH = "";
    private final String DICTIONARY_PATH = "E:\\Users\\Nico\\Desktop\\projet crypto\\Ressources\\liste_francais.txt";
    public int nbOfDictionaryRequests;
    public int nbOfDictionaryComparisons;
    public String[] dictionary;

    public int getNbOfDictionaryRequests() {
        return nbOfDictionaryRequests;
    }

    public void setNbOfDictionaryRequests(int nbOfDictionaryRequests) {
        this.nbOfDictionaryRequests = nbOfDictionaryRequests;
    }

    public int getNbOfDictionaryComparisons() {
        return nbOfDictionaryComparisons;
    }

    public void setNbOfDictionaryComparisons(int nbOfDictionaryComparisons) {
        this.nbOfDictionaryComparisons = nbOfDictionaryComparisons;
    }

    @Override
    public boolean checkWord(String wordToCompare) {
        String[] dictionary = getDictionary();
        nbOfDictionaryRequests++;
        for( String dictionaryWord: dictionary) {
            nbOfDictionaryComparisons++;
            String escapedWord = Pattern.quote(dictionaryWord);
            if(wordToCompare.matches(escapedWord)){
                return true;
            }
        }
        return false;
    }

    @Override
    public byte[] getFileContent(String filePath) {
        try {
            File file = new File(filePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //System.out.println(Arrays.toString(fileContent));
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean writeToFile(String fileName, String fileContent) {
        return false;
    }

    @Override
    public void populateDatabase(String[] words) {

    }

    private fileDAO() {

    }

    public static iDAO getInstance() {
        if(myDAO == null) {
            myDAO = new fileDAO();
        }
        return myDAO;
    }

    public String[] getDictionary() {
        if(dictionary == null) {
            //TODO utiliser une hashmap
            ArrayList<String> dictionary = new ArrayList<>();
            try {
                FileReader fr= new FileReader(DICTIONARY_PATH);   //reads the file
                BufferedReader br= new BufferedReader(fr);  //creates a buffering character input stream
                StringBuffer sb= new StringBuffer();    //constructs a string buffer with no characters
                String line;
                while((line=br.readLine())!=null) {
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
        return dictionary;
    }
}
