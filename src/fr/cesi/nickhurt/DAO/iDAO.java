package fr.cesi.nickhurt.DAO;

public interface iDAO {
    public boolean checkWord(String word);
    public byte[] getFileContent(String fileName);
    public int getNbOfDictionaryRequests();
    public int getNbOfDictionaryComparisons();
    public boolean writeToFile(String fileName, String fileContent);
    public void populateDatabase(String[] words);
}
