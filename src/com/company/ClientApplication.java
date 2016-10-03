package com.company;

import java.io.*;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientApplication {

    private int port;

    public ClientApplication (int port) {
        this.port = port;
    }

    /**
     * Scénario de l'application
     * 1) On récupère la liste des commandes
     * 2) On créé un fichier output.txt
     * 3) On boucle sur chaque commande pour initaliser et exécuter cette commande à envoyer au server
     * 4) On ferme l'écriture du fichier
     **/
    public void scenario() {
        List<String> commands = this.getCommands();
        BufferedWriter bw = this.initializeOutputFile();

        commands.forEach((commandToExec) -> {
            Command command = this.parseAndInitialize(commandToExec);

            this.executeCommand(command, bw);
        });

        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the output file where the responses will be written (output.txt)
     **/
    public BufferedWriter initializeOutputFile() {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./output.txt")));
            return bw;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Récupère la liste des commandes du fichiers source
     * Chaque commande est insérée dans une liste
     **/
    public List<String> getCommands() {

        String fileName =  "./commandes.txt";
        List<String> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

            //br returns as stream and convert it into a List
            list = br.lines().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Parse une commande pour instancier un objet Command avec les bonnes données
     * suivant le type de commande (compilation, chargement,...)
     *
     **/
    public Command parseAndInitialize(String commandToParse) {

        String[] parts = commandToParse.split("\\#");

        Command command = new Command();
        command.setCommand(parts[0]);

        switch (command.getCommand()){
            case "compilation":
                command.setIdentificator(parts[1]);
                command.setFunction(parts[2]);
                break;
            case "chargement":
                command.setIdentificator(parts[1]);
                break;
            case "creation":
                command.setIdentificator(parts[1]);
                command.setFunction(parts[2]);
                break;
            case "ecriture":
                command.setIdentificator(parts[1]);
                command.setFunction(parts[2]);
                command.addValues(parts[3]);
                break;
            case "lecture":
                command.setIdentificator(parts[1]);
                command.setFunction(parts[2]);
                break;
            case "fonction":
                command.setIdentificator(parts[1]);
                command.setFunction(parts[2]);

                // Check if the function has values
                if (parts.length > 3 && parts[3] != null) {

                    List<String> allValues = new ArrayList<String>(Arrays.asList(parts[3].split("\\,")));

                    allValues.forEach((allValue) -> {

                        String[] valueAndType = allValue.split("\\:");
                        command.addTypes(valueAndType[0]);

                        // if the value is between 2 (), we only get the value between it otherwise we can set everything
                        if (valueAndType[1].contains("(") && valueAndType[1].contains(")")) {
                            valueAndType[1] = valueAndType[1].substring(valueAndType[1].indexOf("(") + 1);
                            valueAndType[1] = valueAndType[1].substring(0, valueAndType[1].indexOf(")"));
                        }
                        command.addValues(valueAndType[1]);
                    });
                }
                break;
            default:
                System.out.println("Error");
        }

        return command;
    }

    /**
     * Execute la comande via les sockets
     *
     **/
    public void executeCommand(Command command, BufferedWriter bw) {

        try {

            //Open socket
            Socket socket = new Socket(InetAddress.getLocalHost(), this.port);

            //send command
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
            outToServer.writeObject(command);

            //Read response
            ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());
            String response = inFromServer.readUTF();

            System.out.println("From server : " + response);
            //write response
            bw.write(response);
            bw.flush();
            bw.newLine();

            //close socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);
        System.out.println("Port number: ");
        int port = reader.nextInt();

        ClientApplication client = new ClientApplication(port);
        client.scenario();
    }
}
