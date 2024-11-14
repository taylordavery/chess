package ui;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;

import java.util.Scanner;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.RESET;
import static java.awt.Color.*;
import static ui.EscapeSequences.*;

//public class Repl implements NotificationHandler {
public class Repl {
    private final Client client;

    public Repl(Client client) {
        this.client = client;
    }

    public void run() {
        System.out.println(client.startMsg());
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
//                System.out.print(result + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "\n");
            }
        }
        System.out.println();
    }

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

}