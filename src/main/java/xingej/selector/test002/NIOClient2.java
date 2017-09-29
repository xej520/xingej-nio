package xingej.selector.test002;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient2 {
    public static void main(String[] args) throws Exception {

        String msg = "hello, NIO Server, I'm ";
        int[] ports = {8081, 8082};
        for (int i = 0; i < 10; i++) {
            int index = i % 2;
            int port = ports[index];
            System.out.println("-----port--->:\t" + port);
            new Thread(new SocketChannelThread(msg + i +" client", port)).start();
        }
    }
}

class SocketChannelThread implements Runnable {
    //向服务器发送的消息体
    private String msg;
    private int port;

    public SocketChannelThread(String msg, int port) {
        this.msg = msg;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            SocketChannel clientChannel = SocketChannel.open();
            clientChannel.connect(new InetSocketAddress("localhost", port));
            clientChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(new String(msg).getBytes());
            buffer.flip();
            clientChannel.write(buffer);
            clientChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
