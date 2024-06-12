package tomato.realmshark;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class WebSocket extends WebSocketClient {
    public boolean isConnected = false;
    private String uri;

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        WebSocket ws = new WebSocket("ws://localhost:8080");
        ws.connectBlocking(10, TimeUnit.SECONDS);
        ws.sendString("hello");
        ws.close();
    }

    public WebSocket(String uri) throws URISyntaxException {
        super(new URI(uri));
        this.uri = uri;
    }

    public void sendString(String s) {
        if (!isConnected) return;
        send(s);
    }

    public void sendBytes(byte[] b) {
        if (!isConnected) return;
        send(b);
    }

    public void con() {
        if (isConnected) return;
        try {
            connectBlocking(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection " + handshakedata);
        isConnected = true;
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        isConnected = false;
        closed();
        new Thread(this::reset).start();
    }

    public void reset() {
        try {
            Method method = WebSocketClient.class.getDeclaredMethod("reset");
            method.setAccessible(true);
            Object r = method.invoke(this);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void closed() {
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
