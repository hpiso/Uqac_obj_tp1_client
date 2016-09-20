package com.company;

import java.io.*;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientApplication {

    private int port;

    public ClientApplication (int port) {
        this.port = port;
    }

    public void scenario() {
        System.out.println("Starting scenario");
        Command next = this.getCommand();
        while (next != null) {
            this.executeCommande(next);
        }

    }


    public Command getCommand() {
        //todo
        return null;
    }

    public void executeCommande(Command command) {

        Socket socket;

        try {

            //Open socket
            socket = new Socket(InetAddress.getLocalHost(), this.port);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

            //Send command to server
            outToServer.writeBytes(command.getCommand());

            //close socket
            socket.close();

        }catch (UnknownHostException e) {

            e.printStackTrace();
        }catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Integer port = 2016;

        ClientApplication client = new ClientApplication(port);
        client.scenario();
    }
}
