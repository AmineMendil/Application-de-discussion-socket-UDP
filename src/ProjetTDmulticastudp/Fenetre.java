
package ProjetTDmulticastudp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

// Définition de la classe Fenetre, qui implémente l'interface ActionListener
public class Fenetre implements ActionListener {

    // Déclaration des variables de classe
    public static String nomUtil;
    public int statutsOp;
    public int sortie;
    private JFrame fenetre;
    private JTabbedPane discussions;
    private JPanel panel;
    private ArrayList<JTextArea> discPersonne;
    private ArrayList<String> contacts;
    private ArrayList<String> discContacts;
    private ArrayList<JTextField> textEnvoie;
    private ArrayList<JButton> btnEnvoie;

    // Constructeur de la classe Fenetre
    public Fenetre(int operation) {
        // Initialisation des variables de classe
        statutsOp = operation;
        sortie = 0;
        nomUtil = JOptionPane.showInputDialog("Entrez le nom avec lequel vous souhaitez vous identifier.");

        // Vérification que le nom n'est pas vide
        while (nomUtil.isEmpty()) {
            nomUtil = JOptionPane.showInputDialog("Entrez le nom que vous souhaitez utiliser pour vous identifier.");
        }
        // Création de la fenêtre principale
        fenetre = new JFrame();
        fenetre.setSize(400, 400);
        fenetre.setTitle(nomUtil);
        fenetre.setLocationRelativeTo(null);
        fenetre.setResizable(true);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ew) {
                statutsOp = 1;
                sortie = 1;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Initialisation des listes et des composants de la fenêtre
        discPersonne = new ArrayList<>(); // discusion privé
        discContacts = new ArrayList<>(); // liste des personnes connecter
        contacts = new ArrayList<>();
        textEnvoie = new ArrayList<>();
        btnEnvoie = new ArrayList<>();

        // Initialisation des composants graphiques de la fenêtre
        initialiserComposants();
        fenetre.setVisible(true);

    }

    // Méthode pour initialiser les composants graphiques de la fenêtre
    private void initialiserComposants() {
        panel = new JPanel();
        JLabel titre = new JLabel("Vos discussions : " + nomUtil);

        panel.setLayout(null);
        fenetre.getContentPane().add(panel);
        titre.setBounds(10, 10, 160, 30);
        panel.add(titre);

        setBouttons();
        setGeneral();
    }

    // Méthode pour ajouter un bouton à la fenêtre
    private void setBouttons() {
        JButton nvDiscussion = new JButton();
        nvDiscussion.addActionListener(this);
        nvDiscussion.setText("Nouvelle discussion privée");
        nvDiscussion.setBounds(180, 10, 200, 30);
        panel.add(nvDiscussion);
    }

    // Méthode pour ajouter un onglet "Général" à la fenêtre
    private void setGeneral() {
        discussions = new JTabbedPane();
        discussions.setBounds(10, 50, 370, 300);
        nvDiscussion("Géneral");
        panel.add(discussions);

    }

    // Méthode pour créer une nouvelle discussion privé
    private void nvDiscussion(String nomUtil) {

        //  Création d'un nouveau panneau pour la discussion et définition d'un layout null 
        JPanel nvPanel = new JPanel();
        nvPanel.setLayout(null);

        // Ajout du panneau de discussion à l'onglet discussions avec le titre nomUtil.
        discussions.add(nomUtil, nvPanel);

        // Création d'une zone de texte pour la conversation, non-éditable, avec du texte noir, ajoutée à la liste discPersonne.
        JTextArea conversation = new JTextArea();
        conversation.setEditable(false);
        conversation.setForeground(Color.BLACK);
        discPersonne.add(conversation);

        //Création d'un JScrollPane pour la zone de texte de la conversation, positionné à l'intérieur du panneau.
        JScrollPane scrollPane = new JScrollPane(discPersonne.get(discPersonne.size() - 1));
        scrollPane.setBounds(10, 10, 350, 200);
        nvPanel.add(scrollPane);

        //Création d'un champ de texte pour l'envoi de messages, ajouté à la liste textEnvoie, positionné dans le panneau.
        JTextField texto = new JTextField();
        textEnvoie.add(texto);
        textEnvoie.get(textEnvoie.size() - 1).setBounds(10, 220, 250, 39);
        nvPanel.add(textEnvoie.get(textEnvoie.size() - 1));

        //Création d'un bouton d'envoi, ajouté à la liste btnEnvoie, positionné dans le panneau, ajout d'un ActionListener pour gérer les actions.
        JButton envoie = new JButton("Envoyer");
        btnEnvoie.add(envoie);
        btnEnvoie.get(btnEnvoie.size() - 1).setBounds(270, 220, 85, 38);
        btnEnvoie.get(btnEnvoie.size() - 1).setText("Envoyer");
        btnEnvoie.get(btnEnvoie.size() - 1).addActionListener(this);

        // Définition du nom du bouton en fonction de sa position dans la liste,  et l'ajout  au panneau.
        String nomBtn = "Envoyer " + (btnEnvoie.size() - 1);
        System.out.println(nomBtn);
        btnEnvoie.get(btnEnvoie.size() - 1).setName(nomBtn);
        nvPanel.add(btnEnvoie.get(btnEnvoie.size() - 1));

        //  Ajout du nom de l'utilisateur à la liste des contacts de discussion.
        discContacts.add(nomUtil);
    }

    // Méthode pour obtenir le statut de l'opération
    public int getStatutsOp() {
        return statutsOp;
    }

    // Méthode pour obtenir la sortie
    public int getSortie() {
        return sortie;
    }
    // Méthode pour définir le statut de l'opération avec les paramèttres

    public void setStatusOp(int nvStatusOp) {
        statutsOp = nvStatusOp;
    }

    // Méthode pour obtenir l'onglet actif
    public int getActiveTab() {
        return discussions.getSelectedIndex();
    }

    // Méthode pour obtenir le message actif
    public String getActiveMessage() {
        int selectedIndex = discussions.getSelectedIndex();
        String texto = textEnvoie.get(selectedIndex).getText();
        textEnvoie.get(selectedIndex).setText("");
        return texto;
    }

    // Méthode pour obtenir le contact
    public String getdiscContacts(int i) {
        return discContacts.get(i);
    }

    // Méthode pour définir un nouveau message
    public void setNvMessage(String message) {

        //Vérifie si le message contient la balise <contacts> pour mettre à jour la liste des contacts.
        if (message.contains("<contacts>")) {

            //Déclaration d'une variable contact pour stocker temporairement les noms de contacts et nettoyage de la liste des contacts.
            String contact = "";
            contacts.clear();

            // Boucle à travers le message à partir de l'indice 11 pour parcourir les noms de contacts.
            for (int i = 11; i < message.length(); i++) {
                System.out.println("|" + message.charAt(i) + "|");
                // Si le caractère est une lettre, il est ajouté au nom du contact actuel.
                if (Character.isLetter(message.charAt(i))) {
                    contact += message.charAt(i);
                } else if (message.charAt(i) == ',') {
                    // Si le caractère est une virgule, le nom du contact actuel est ajouté à la liste des contacts et la variable contact est réinitialisée.
                    contacts.add(contact);
                    contact = "";
                } else {
                }
            }
            contacts.add(contact);
            System.out.println(contacts);
        } else if (message.startsWith("S<message>")) {
            //Vérifie si le message commence par S<message> pour traiter un nouveau message reçu.
            String expediteur = "";
            message = message.substring(10);

            // :) , :D, :3, :P, :(  ,:'(,D:, >:c
            //Emoji
            message = message.replace(":)", "\uD83D\uDE04");
            message = message.replace(":D", "\uD83D\uDE03");
            message = message.replace(":3", "\uD83D\uDE0A");
            message = message.replace(":P", "\uD83D\uDE1C");
            message = message.replace(":(", "\uD83D\uDE14");
            message = message.replace(":'(", "\uD83D\uDE22");
            message = message.replace("D:", "\uD83D\uDE29");
            message = message.replace(">:c", "\uD83D\uDE21");

            // Vérifie si le message contient la balise <prive> pour traiter un message privé.
            if (message.contains("<prive>")) {
                //Récupération du nom de l'expéditeur du message.
                String destinataire = "";
                int i = 1;

                message = message.substring(7);

                while (Character.isLetter(message.charAt(i))) {
                    expediteur = expediteur + message.charAt(i);
                    i++;
                }
                //Récupération du nom du destinataire du message.
                message = message.substring(i + 1);

                i = 1;
                System.out.println(message);
                while (Character.isLetter(message.charAt(i))) {
                    destinataire = destinataire + message.charAt(i);
                    i++;
                }

                // Affichage de l'expéditeur et du destinataire du message.
                message = message.substring(i + 1);
                System.out.println(message);
                System.out.println("Expiediteur: " + expediteur + " destinataire: " + destinataire);

                // Vérifie si l'utilisateur actuel est le destinataire du message privé.
                if (nomUtil.equals(destinataire)) {
                    /* Si l'utilisateur est le destinataire, le message est 
                   affiché dans la discussion appropriée. Sinon, si l'utilisateur
                   est l'expéditeur, le message est affiché dans la discussion active*/
                    if (discContacts.contains(expediteur)) {
                        int selectedIndex = discContacts.indexOf(expediteur);
                        textEnvoie.get(selectedIndex).setText("");
                        discPersonne.get(selectedIndex).setText(discPersonne.get(selectedIndex).getText()
                                + "\n" + expediteur + ":" + message);
                    } else {
                        nvDiscussion(expediteur);
                        discussions.setSelectedIndex(discContacts.indexOf(expediteur));
                        int selectedIndex = discussions.getSelectedIndex();
                        textEnvoie.get(selectedIndex).setText("");
                        discPersonne.get(selectedIndex).setText(discPersonne.get(selectedIndex).getText()
                                + "\n" + expediteur + ":" + message);
                    }
                } else if (nomUtil.equals(expediteur)) {
                    int selectedIndex = discussions.getSelectedIndex();
                    textEnvoie.get(selectedIndex).setText("");
                    discPersonne.get(selectedIndex).setText(discPersonne.get(selectedIndex).getText()
                            + "\n" + expediteur + ":" + message);
                }
            } else {
                // Si le message n'est pas privé, il est affiché dans la discussion générale.
                int selectedIndex = 0;
                textEnvoie.get(selectedIndex).setText("");
                discPersonne.get(selectedIndex).setText(discPersonne.get(selectedIndex).getText()
                        + "\n" + message);
            }
        }
    }

    // Méthode pour obtenir le nom de l'utilisateur
    public String getNomUtil() {
        return nomUtil;
    }

    // Méthode appelée lorsqu'un bouton est cliqué
    @Override
    public void actionPerformed(ActionEvent ae) {
        // Récupération du bouton sur lequel l'utilisateur a cliqué.
        JButton boutton = (JButton) ae.getSource();
        //Vérification si l'action est de créer une nouvelle discussion privée.
        if (boutton.getActionCommand().equals("Nouvelle discussion privée")) {
            //Affichage d'un message d'erreur si aucun contact n'est en ligne.
            if (contacts.toArray().length == 0) {
                JOptionPane.showMessageDialog(null, "Actuellement, il n'y a aucun contact en ligne.", "Contacts",
                        JOptionPane.ERROR_MESSAGE);

            } else {
                // Affichage d'une fenêtre pour sélectionner l'utilisateur avec qui démarrer une nouvelle discussion.
                ArrayList<String> affContacts = contacts;
                affContacts.remove(nomUtil);
                String selection = (String) JOptionPane.showInputDialog(null, "Nouvelle discussion", "Sélectionnez l'utilisateur avec lequel vous souhaitez créer une discussion",
                        JOptionPane.INFORMATION_MESSAGE, null, affContacts.toArray(), affContacts.toArray()[0]);

                // Sélection de l'onglet de discussion approprié ou création d'une nouvelle discussion.
                if (discContacts.contains(selection)) {
                    discussions.setSelectedIndex(discContacts.indexOf(selection));
                } else {
                    nvDiscussion(selection);
                    discussions.setSelectedIndex(discContacts.indexOf(selection));
                }
                statutsOp = 0;
            }

            // Si l'action est d'envoyer un message, le statut d'opération est mis à 1.
        } else if (boutton.getActionCommand().equals("Envoyer")) {
            statutsOp = 1;
        }

    }

    public static void main(String[] args) {
        // new Fenetre(0);
    }

}
