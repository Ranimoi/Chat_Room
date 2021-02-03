package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    static Socket clientSocket;
    static JTextArea receivedTextArea;
    static JComboBox usersComboBox;
    static JTextField textField;

    public static void main(String[] args) throws Exception {

        //Create the GUI frame and components
        JFrame frame = new JFrame("Chatting Client");
        frame.setLayout(null);
        frame.setBounds(100, 100, 600, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameLabel = new JLabel("Client Name");
        nameLabel.setBounds(20, 40, 150, 30);
        frame.getContentPane().add(nameLabel);

        textField = new JTextField();
        textField.setBounds(100, 40, 200, 30);
        frame.getContentPane().add(textField);

        JButton connect = new JButton("Connect");
        connect.setBounds(300, 40, 100, 30);
        frame.getContentPane().add(connect);
        
        JLabel sendTo = new JLabel("Send To");
        sendTo.setBounds(20, 400, 100, 30);
        frame.getContentPane().add(sendTo);
        sendTo.setVisible(false);
        
	   JTextField sendTextField = new JTextField();
	    sendTextField.setBounds(130, 400, 150, 30);
	    frame.getContentPane().add(sendTextField);
	    sendTextField.setVisible(false);

	   
	    
	    JButton sendButton = new JButton("Send");
        sendButton.setBounds(420, 450, 80, 30);
        frame.getContentPane().add(sendButton);
        sendButton.setVisible(false);

        JTextArea jTextArea = new JTextArea();
        jTextArea.setBounds(20, 425, 400, 75);
        frame.getContentPane().add(jTextArea);
        jTextArea.setVisible(false);
        

        

        receivedTextArea = new JTextArea();
        receivedTextArea.setBounds(20, 100, 460, 300);
        receivedTextArea.setEditable(false);
        frame.getContentPane().add(receivedTextArea);

        usersComboBox = new JComboBox();
        usersComboBox.setBounds(100, 400, 200, 30);
        frame.getContentPane().add(usersComboBox);
        usersComboBox.setVisible(false);

        JScrollPane receivedTextAreaScroll = new JScrollPane(receivedTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        receivedTextAreaScroll.setBounds(20, 100, 460, 300);
        frame.getContentPane().add(receivedTextAreaScroll);

        //Action listener when connect button is pressed
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().equals("")) {
                    try {
                        if (connect.getText().equals("Connect")) { //if pressed to connect

                            //create a new socket to connect with the server application
                            clientSocket = new Socket("localhost", 6789);

                            //call function StartThread
                            StartThread();

                            //make the GUI components visible, so the client can send and receive messages
                            sendButton.setVisible(true);
                            sendTo.setVisible(true);
                            jTextArea.setVisible(true);
                            //usersComboBox.setVisible(true);
                            textField.setEnabled(false);
                            receivedTextArea.setVisible(true);
                            //receivedTextArea.setText("You are now connected");
                            receivedTextAreaScroll.setVisible(true);
                            sendTextField.setVisible(true);

                            //change the Connect button text to disconnect
                            connect.setText("Disconnect");

                        } else { //if pressed to disconnect

                            //create an output stream and send a Remove message to disconnect from the server
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes("-Remove\n");

                            //close the client's socket
                            clientSocket.close();

                            //make the GUI components invisible
                            sendButton.setVisible(false);
                            sendTo.setVisible(false);
                            jTextArea.setVisible(false);
                            sendTextField.setVisible(false);
                            usersComboBox.setVisible(false);
                            textField.setEnabled(true);
                            //receivedTextArea.setText("");
                            //jTextArea.setText("");
                            //receivedTextArea.setVisible(false);
                            //receivedTextAreaScroll.setVisible(false);
                            //sendTo.setVisible(false);
                            //change the Connect button text to connect
                            connect.setText("Connect");

                        }

                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                }
            }
        });

        //Action listener when send button is pressed
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    //create an output stream
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                    Object selectedItem = usersComboBox.getSelectedItem();
                   
                  
                    String text = jTextArea.getText();
                    String sendtowho = sendTextField.getText();
                    String from= textField.getText();
                    String sendingSentence = "-Message,"+ text + ","+ sendtowho+","+from+ "\n";

                    

                    sendingSentence += "\n";

                    outToServer.writeBytes(sendingSentence);
                    
                
                  

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        });

        //Disconnect on close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {

                try {

                    //create an output stream and send a Remove message to disconnect from the server
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("-Remove\n");

                    //close the client's socket
                    clientSocket.close();

                    //make the GUI components invisible
                    sendButton.setVisible(false);
                    sendTo.setVisible(false);
                    jTextArea.setVisible(false);
                    receivedTextArea.setVisible(false);
                    receivedTextAreaScroll.setVisible(false);

                    //change the Connect button text to connect
                    connect.setText("Connect");

                    System.exit(0);

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        
    
            
        });

        frame.setVisible(true);
        }

        

    //Thread to always read messages from the server and print them in the textArea
    private static void StartThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //create a buffer reader and connect it to the socket's input stream
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String receivedSentence;

                    //always read received messages and append them to the textArea
                    while (true) {

                        receivedSentence = inFromServer.readLine();
                        //System.out.println(receivedSentence);

                        if (receivedSentence.startsWith("-Connected")) {
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

                            String sendingSentence = "-Name," + textField.getText() + "\n";
                            outToServer.writeBytes(sendingSentence);
                        } else if (receivedSentence.startsWith("-NewUser")) {

                            String[] strings = receivedSentence.split(",");
                            String newUser = strings[1];
                           if (newUser.equals(textField.getText())) {
                                receivedTextArea.append("You are connected\n");
                           }
                          else {
                                receivedTextArea.append(newUser + " is connected\n");
                           }

                        } else if (receivedSentence.startsWith("-NewMessage")) {
                            String[] strings = receivedSentence.split(",");
                            String message = strings[1];
                            String user = strings[2];
                            String messageTo = strings[3];
                            String messageFrom = strings[4];

                            String receivedMessage = "";

                            if (messageTo.equals(textField.getText())) {
                                receivedMessage += messageFrom + ": ";
                                receivedMessage += message +" (private)" + "\n";
                                receivedTextArea.append(receivedMessage);
                            } else if (messageFrom.equals(textField.getText())) {
                                if (messageTo.equals("")) {
                                    receivedMessage += "You to everyone: ";
                                } else {
                                	
                                	  
                                    receivedMessage += "You to " + messageTo + ": ";
                                  
                                }
                                receivedMessage += message +"\n";
                                receivedTextArea.append(receivedMessage);
                           
                            } else {
                                receivedMessage += user + ": ";
                                
                                if (messageTo.equals("")) {
                                receivedMessage += message + " (public)" +"\n";
                                }
                
                                receivedTextArea.append(receivedMessage);
                            }

                            
                        }
                    
                  

                } 
              }
               
                    catch (Exception ex) {

                }


            
        }}).start();

    }
}