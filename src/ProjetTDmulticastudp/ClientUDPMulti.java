/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjetTDmulticastudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 *
 * @author useur
 */
public class ClientUDPMulti  extends Thread {

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
    
    Fenetre f = new Fenetre(0);

    // Définition de la méthode run() qui sera exécutée lorsque le thread démarrera. Récupération de l'adresse IP du groupe multicast.
    public void run() {
       
        InetAddress groupe = null;// Initialise l'objet InetAddress à null
        try {
             // Utilise la méthode getByName() de la classe InetAddress pour obtenir une instance InetAddress correspondant à l'adresse multicast MCAST_ADDR
            groupe = InetAddress.getByName(MCAST_ADDR);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        boolean saute = true;
        try {
            //Création d'une socket multicast et ajout au groupe. 
            MulticastSocket socket = new MulticastSocket(MCAST_PORT);
            socket.joinGroup(groupe);
            DatagramPacket contact = new DatagramPacket(("<debut>" + f.getNomUtil()).getBytes(), ("<debut>" + f.getNomUtil()).length(), groupe, MCAST_PORT);
            //Envoi d'un message de contact pour informer le groupe de la présence du client.
            socket.send(contact);

            /*Boucle principale qui écoute les messages entrants lorsque le 
            statut d'opération est à 0 (en attente de messages) et envoie les messages
            lorsque le statut d'opération est à 1 (envoi de messages).*/
            while (saute) {
                
                // Vérifie si le statut de l'opération est égal à 0
                if (f.getStatutsOp() == 0) {
                    socket.setSoTimeout(100);// Définit une durée maximale d'attente de 100 millisecondes pour la réception des données
                    try {
                        // Déclare un tableau de bytes pour stocker les données reçues
                        byte[] buff = new byte[BUF_LEN];
                        DatagramPacket recevoire = new DatagramPacket(buff, buff.length);// Crée un DatagramPacket pour recevoir les données
                        socket.receive(recevoire);// Attend la réception d'un DatagramPacket sur le socket
                        byte[] donnee = recevoire.getData();// Extrait les données reçues du DatagramPacket
                        String message = new String(donnee);// Convertit les données en une chaîne de caractères
                        System.out.println("Données reçues: " + message);// Affiche les données reçues
                        f.setNvMessage(message);// Met à jour le nouveau message dans l'objet f (supposément une référence à un autre objet dans le contexte de l'application)
                    } catch (Exception e) {
                    }

                }else if(f.getStatutsOp() == 1){// Vérifie le statut de sortie
                    String message = ""; 
                    if (f.getSortie() == 1) {// Si la sortie est 1, construit un message de sortie
                        message = "<sortie>"+f.getNomUtil();// Si l'onglet actif n'est pas égal à 0, construit un message privé
                    }else{
                        // Si l'onglet actif n'est pas égal à 0, construit un message privé
                        if(f.getActiveTab() != 0){
                            message = "C<message><prive><"+f.getNomUtil()+"><"+f.getdiscContacts(f.getActiveTab())+">"+f.getActiveMessage();
                            
                        // Si l'onglet actif est égal à 0, construit un message général
                        }else if(f.getActiveTab() == 0){
                            message = "C<message><"+f.getNomUtil()+">"+f.getActiveMessage();
                        }
                    }
                    
                    // Crée un DatagramPacket à envoyer avec le message construit
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), groupe, MCAST_PORT);
                    
                    // Affiche les détails de l'envoi
                    System.out.println("Envoi: "+message+" avec un TTL = "+socket.getTimeToLive());
                    
                    // Envoie le paquet via le socket
                    socket.send(packet);
                    
                    // Met à jour le statut de l'opération à 0
                    f.setStatusOp(0);  
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void main(String[] args) {

        try {
            // Crée une instance de la classe ClientUDPMulti qui hérite de la classe Thread
            ClientUDPMulti client = new ClientUDPMulti();
            
            //Démarrer le client
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


