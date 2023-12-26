import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class InterfaceClientAdmin {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel homePanel;
    private JPanel clientPanel;
    private JPanel adminPanel;

    private JTextField nomField;
    private JTextField prenomField;
    private JComboBox<String> specialiteComboBox;
    private JComboBox<String> clubComboBox;
    private JTable dataTable;

    // Variables pour la connexion à la base de données MySQL
    private final String url = "jdbc:mysql://localhost/java_project?serverTimezoone=UTC";
    private final String utilisateur = "Ahmed";
    private final String motDePasse = "tanitbeycesar56";

    public InterfaceClientAdmin() {
        frame = new JFrame("Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);

        mainPanel = new JPanel(new CardLayout());

        // Création de la page d'accueil
        homePanel = createHomePanel();
        mainPanel.add(homePanel, "Accueil");

        // Création des interfaces Client et Admin (partiellement implémentées)
        clientPanel = createClientPanel();
        mainPanel.add(clientPanel, "Client");
        clientPanel.setSize(60, 40);


        adminPanel = createAdminPanel();
        mainPanel.add(adminPanel, "Admin");
        adminPanel.setSize(60,40);
        adminPanel.setBounds(250, 100, 60, 40);


        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        JButton clientButton = new JButton("Client");
        JButton adminButton = new JButton("Admin");
        clientButton.setSize( 60, 40);  

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Client");
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Admin");
            }
        });

        panel.add(clientButton,BorderLayout.EAST);
        panel.add(adminButton, BorderLayout.WEST);

        return panel;
    }

    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new GridLayout(0,1));
        JButton retourButton = new JButton("Retour à l'accueil");
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Accueil");}
        });

        panel.add(retourButton, BorderLayout.NORTH);
        nomField = new JTextField(20);
        prenomField = new JTextField(20);

        String[] specialites = {"nan","bd", "im", "av", "cm"};
        specialiteComboBox = new JComboBox<>(specialites);

        String[] clubs = {"nan" , "orenda", "microsoft", "log", "j2i", "spark", "robotique"};
        clubComboBox = new JComboBox<>(clubs);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enregistrerInformations(nomField.getText(), prenomField.getText(), specialiteComboBox.getSelectedItem().toString(), clubComboBox.getSelectedItem().toString());   
            }
        });
        JButton annulerButton= new JButton("Annuler");
        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Accueil");
            }
        });
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("Spécialité:"));
        panel.add(specialiteComboBox);
        panel.add(new JLabel("Club:"));
        panel.add(clubComboBox);
        panel.add(okButton);
        panel.add(annulerButton);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel();
        JButton retourButton = new JButton("Retour à l'accueil");
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Accueil");}
        });
        String[] columnNames = {"Nom", "Prenom", "Spécialité", "Club"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane);
        panel.add(retourButton);
        fetchDataFromDatabase();

        return panel;}
 


    private  void fetchDataFromDatabase() {
            try {
                // Establishing a connection
                Connection connection = DriverManager.getConnection(url, utilisateur, motDePasse);

                // Creating a statement
                Statement statement = connection.createStatement();

                // Executing a query to retrieve data (replace "info" with your actual table name)
                String query = "SELECT * FROM form_client";
                ResultSet resultSet = statement.executeQuery(query);

                // Get the table model to add rows
                DefaultTableModel tableModel = (DefaultTableModel) dataTable.getModel();

                // Clear existing data in the table
                tableModel.setRowCount(0);

                // Populate the table with data
                int numCols = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= numCols; i++) {tableModel.addColumn(resultSet.getMetaData().getColumnName(i));}
                while (resultSet.next()) {
                    Object[] rowData = new Object[numCols];
                    for (int i = 0; i < numCols; i++) {rowData[i] = resultSet.getObject(i + 1);}
                    tableModel.addRow(rowData); }

                // Closing resources
                resultSet.close();
                statement.close();
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    private void enregistrerInformations(String nom , String prenom, String specialite, String club) {
        try {
            Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse);
            
            
            String query = "INSERT INTO form_client (Nom, Prenom, Spécialité, Club) VALUES (?,?, ?, ?)";
            PreparedStatement preparedStatement = connexion.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, specialite);
            preparedStatement.setString(4, club);

            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Informations enregistrées avec succès !");

            // Fermeture des ressources
            preparedStatement.close();
            connexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors de l'enregistrement des informations.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfaceClientAdmin();
            }
        });
    }
}
