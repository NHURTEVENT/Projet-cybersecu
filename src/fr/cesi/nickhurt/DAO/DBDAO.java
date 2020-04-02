package fr.cesi.nickhurt.DAO;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class DBDAO implements iDAO{
    public int nbOfDictionaryRequests;
    public int nbOfDictionaryComparisons;
    private Connection con;
    private static iDAO dbDAO;

    private DBDAO() {
//        String DBurl = "jdbc:mysql://localhost/bruteforce";
        String DBurl = "jdbc:mysql://localhost/bruteforce";
//
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            con = DriverManager.getConnection(DBurl, "root", "");
//            System.out.println(con.getCatalog());
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        nbOfDictionaryRequests = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bruteforce", "root", "");
            checkWord("nico");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        public static iDAO getInstance() {
        if(dbDAO == null) {
            dbDAO = new DBDAO();
        }
        return dbDAO;
    }

    @Override
    public boolean checkWord(String wordToCheck) {
        ResultSet result = null;
//        String request = "SELECT word FROM dictionary WHERE word LIKE '"+wordToCheck+"'";
        String request = "SELECT word FROM dictionary WHERE MATCH (word) AGAINST ('"+wordToCheck+"' IN NATURAL LANGUAGE MODE)";
        nbOfDictionaryRequests++;
        try {
            Statement stmt = con.createStatement();
            result = stmt.executeQuery(request);
            if(result.next()) {
//                System.out.println(result.getString("word"));
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public void populateDatabase(String[] words) {
        for(String word: words) {
            String query = "INSERT INTO `dictionary` (`word_id`, `word`) VALUES (NULL, ?)";

            try {
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setString(1,word);
                preparedStmt.execute();
                nbOfDictionaryRequests++;
            } catch (SQLException e) {
            System.out.println(e);
            }
        }
    }

    @Override
    public byte[] getFileContent(String fileName) {
        return new byte[0];
    }

    @Override
    public int getNbOfDictionaryRequests() {
        return nbOfDictionaryRequests;
    }

    @Override
    public int getNbOfDictionaryComparisons() {
        return nbOfDictionaryComparisons;
    }

    @Override
    public boolean writeToFile(String fileName, String fileContent) {
        return false;
    }
}
