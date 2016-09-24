package com.company;

import java.io.*;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientApplication {

    private int port;

    public ClientApplication (int port) {
        this.port = port;
    }

    public void scenario() {
        System.out.println("Starting scenario");
        List<String> commands = this.getCommands();

        commands.forEach((commandToExec) -> {
            Command command = new Command();
            command.setCommand(commandToExec);

            this.executeCommand(command);
        });
    }

    public List<String> getCommands() {

        String fileName =  "/Users/hugopiso/Projects/uqac/java_client/commandes.txt";
        List<String> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

            //br returns as stream and convert it into a List
            list = br.lines().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void executeCommand(Command command) {

        try {

            //Open socket
            Socket socket = new Socket(InetAddress.getLocalHost(), this.port);
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader inFromServer  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send command to server
            outToServer.writeObject(command);

            //Get response
            String response = inFromServer.readLine();
            System.out.println("From server" + response);

            //close socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Integer port = 2016;

        ClientApplication client = new ClientApplication(port);
        client.scenario();
    }
}
