/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjetTDmulticastudp;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author useur
 */
public class ServerUDPMulti  extends Thread {

    public static final String MCAST_ADDR = "230.1.1.1"; 
    // Définit une constante représentant l'adresse de multicast à laquelle le programme va se joindre.
    // L'adresse de multicast est une adresse IP spéciale utilisée pour envoyer des messages à un groupe de destinataires sur un réseau.
    // Dans ce cas, l'adresse de multicast est "230.1.1.1".

    public static final int MCAST_PORT = 4000;
    // Définit une constante représentant le numéro de port sur lequel le programme va écouter les messages multicast.
    // Le numéro de port est une valeur numérique qui identifie un point final sur un réseau.
    // Dans ce cas, le numéro de port multicast est 4000.
    public static final int BUF_LEN = 2048;
    // Définit une constante représentant la taille du tampon (buffer) utilisé pour stocker les données reçues.
    // Le tampon est une zone de mémoire utilisée pour stocker temporairement des données.
    // Dans ce cas, la taille du tampon est fixée à 2048 octets (2 Ko), ce qui est généralement une taille adéquate pour la réception de messages réseau.
    
    private ArrayList<String> contacts;//Liste de chaine de caractère pour stocker les contacts

    /*Définition de la méthode run() qui sera exécutée lorsque le thread démarrera.
    Initialisation de la liste des contacts et récupération de 
    l'adresse IP du groupe multicast.*/
    public void run() {
        
        contacts = new ArrayList<>();// Initialise une nouvelle instance de ArrayList pour stocker les contacts.
        String message = "";// Initialise une chaîne de caractères vide.
        InetAddress groupe = null;// Initialise une variable InetAddress à null. qui servira pour stocker l'adresse IP multicast

        try {
            groupe = InetAddress.getByName(MCAST_ADDR);// récupération de l'adresse IP Multicast 
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        /*Création d'une boucle infinie pour écouter en permanence les
        messages entrants. Création d'une socket multicast et ajout au groupe. 
        Réception d'un paquet contenant les données du message.*/
        for (;;) {
            try {
                
                MulticastSocket socket = new MulticastSocket(MCAST_PORT);// Crée un nouveau MulticastSocket qui va écouter les paquets multicast sur le port spécifié par la constante MCAST_PORT.
                socket.joinGroup(groupe);// Rejoint le groupe multicast spécifié par l'adresse IP dans la variable groupe.
                System.out.println("En attente....");

                
                byte[] buff = new byte[BUF_LEN];// Déclare un tableau de bytes pour stocker les données reçues
                DatagramPacket packetRecue = new DatagramPacket(buff, buff.length); // Crée un DatagramPacket pour recevoir les données
                socket.receive(packetRecue);// Attend la réception d'un DatagramPacket sur le socket
                byte[] donnee = packetRecue.getData();// Extrait les données reçues du DatagramPacket
                message = new String(donnee);// convertir les donnée reçue
                System.out.println("Message Reçue: " + message);

                
                // Vérifie si le message contient la chaîne "<debut>".
                if (message.contains("<debut>")) {
                    
                    message = message.substring(7);// Supprime les 7 premiers caractères du message.
                    String nombre = "";
                    int i = 0;
                    
                    // Parcourt les caractères du message jusqu'à ce qu'un caractère qui n'est pas une lettre soit trouvé.
                    while (Character.isLetter(message.charAt(i))) {
                        nombre = nombre + message.charAt(i);
                        i++;
                    }
                   
                    contacts.add(nombre);// Ajoute `nombre` à la liste de contacts.
                    String cont = "<contacts>" + contacts.toString();// Construit un nouveau message contenant la liste de contacts mise à jour.
                    //Envoie au contacts.
                    System.out.println("Envoyer: " + cont);
                   
                    DatagramPacket packet = new DatagramPacket(cont.getBytes(), cont.length(), groupe, MCAST_PORT);
                    socket.send(packet);
                    socket.close();
                    
                // Si le message contient la chaîne "C<message>".
                } else if (message.contains("C<message>")) {
                    message = message.substring(1);// Supprime le premier caractère du message.
                    message = "S" + message;// Ajoute "S" au début du message pour indiquer qu'il s'agit d'un message de serveur.
        
                    // Envoie le message aux destinataires multicast.
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), groupe, MCAST_PORT);
                    System.out.println("Envoyer: " + message.toString() + " avec un TTL = " + socket.getTimeToLive());
                    socket.send(packet);
                    socket.close();
                    
                // Si le message contient la chaîne "<sortie>".
                } else if (message.contains("<sortie>")) {
                    String sortie = "";
                    int i = 8;
                    
                    // Parcourt les caractères du message pour obtenir le nom de l'utilisateur qui quitte.
                    while (Character.isLetter(message.charAt(i))) {
                        sortie = sortie + message.charAt(i);
                        i++;
                    }
                    contacts.remove(sortie);// Retire l'utilisateur de la liste de contacts.
                    String cont = "<contacts>" + contacts.toString();// Construit un nouveau message contenant la liste de contacts mise à jour.
                    
                    // Envoie le nouveau message aux contacts.
                    DatagramPacket packet = new DatagramPacket(cont.getBytes(), cont.length(), groupe, MCAST_PORT);
                    socket.send(packet);
                    socket.close();
                }
                // Envoyer les données reçues à tout le groupe.

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(2);
            }
           
        }
    }

    public static void main(String[] args) {
        try {
            // Crée une instance de la classe ServerUDPMulti qui hérite de la classe Thread.
            ServerUDPMulti server = new ServerUDPMulti();
            
            server.start();//Démarre le serveur
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



