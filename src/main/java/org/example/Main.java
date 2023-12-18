package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import  java.awt.*;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;

import static java.lang.System.exit;

public class Main {
    private static JLabel labelRes;
    public static void main(String[] args) throws InterruptedException{
        String path = "http://localhost:8080/api";

        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JTextField inputField = new JTextField();
        inputField.setFont((new Font("Arial", Font.PLAIN, 30)));

        JLabel labelField = new JLabel("Input leitor", SwingConstants.CENTER);
        labelField.setFont((new Font("Arial", Font.BOLD, 30)));

        labelRes = new JLabel("", SwingConstants.CENTER);
        labelRes.setFont((new Font("Arial", Font.ITALIC, 25)));


        inputField.getDocument().addDocumentListener(new DocumentListener() {
             @Override
             public void insertUpdate(DocumentEvent e) {
                 if(inputField.getText().length() == 10){
                 resServidor(path, panel, inputField);
                 }
             }

             @Override
             public void removeUpdate(DocumentEvent e) {
//                 resServidor(path, panel, inputField);
             }

             @Override
             public void changedUpdate(DocumentEvent e) {
             }
         });

        panel.add(labelField);
        panel.add(labelRes);
        panel.add(inputField);

        JButton sair = new JButton("Sair");
        sair.setFont(new Font("Arial", Font.PLAIN, 30));

        sair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        panel.add(sair);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void resServidor(String path, JPanel panel, JTextField inputField) {
        String resposta;
        try {
            resposta = inputField.getText();

            String content = "{\"ACK\" : \"" + resposta + "\"}";
//                            System.out.println(content);

            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(content);
            out.flush();
            out.close();

            int responseCode = connection.getResponseCode();
            System.out.println("Código de Resposta: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Erro inesperado");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            JsonObject jsonObject = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                JsonElement jsonElement = JsonParser.parseString(line);
                jsonObject = jsonElement.getAsJsonObject();
                resposta = jsonObject.get("ACK").getAsString();

                if (resposta.contains("1")) {
                    System.out.println("validado");
                    labelRes.setText("Validado!");
                    panel.setBackground(Color.green);
                    Timer timer = new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            panel.setBackground(null);
                            labelRes.setText("");
                            inputField.setText("");
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    System.out.println("não validado");
                    labelRes.setText("Código inválido!");
                    panel.setBackground(Color.red);
                    Timer timer = new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            panel.setBackground(null);
                            labelRes.setText("");
                            inputField.setText("");
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();

                }
            }

            in.close();}
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch(NumberFormatException ex){
            ex.printStackTrace();
            inputField.setText("Erro: Entrada invalida");
        }
    }
}