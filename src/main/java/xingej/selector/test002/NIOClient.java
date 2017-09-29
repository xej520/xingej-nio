package xingej.selector.test002;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress("localhost", 8081));
        clientChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(new String ("hello, server! ").getBytes());
        buffer.flip();
        clientChannel.write(buffer);
        clientChannel.close();
    }
}
