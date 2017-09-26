package xingej.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelTest {

    public void connectServer() throws IOException{
        // 创建一个 SocketChannel对象，
        // 请注意，并没有进行 链接服务器端哦
        SocketChannel socketChannel = SocketChannel.open();

        //开始链接服务器端
        socketChannel.connect(new InetSocketAddress("localhost", 8081));

        //在客户端创建 字节缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        String msg = "\nhello, nio, hello ,spark, hello ,hadoop, flume, mesos, marathon, netty, mina, stream, inputstream, outputstream \n" +
                "hello, nio, hello ,spark, hello ,hadoop, flume, mesos, marathon, netty, mina, stream, inputstream, outputstream \n" +
                "hello, nio, hello ,spark, hello ,hadoop, flume, mesos, marathon, netty, mina, stream, inputstream, outputstream 北京\n";

        //往字节缓存区，添加数据
        byteBuffer.put(msg.getBytes());
        // 针对是更新limit值，将此值更新为position了，用于接下来的读操作
        byteBuffer.flip();
        while(byteBuffer.hasRemaining()) {
            //将字节缓存里的数据，写到管道中去
            socketChannel.write(byteBuffer);
        }

        socketChannel.close();
    }

    public static void main(String[] args) throws IOException{
        new SocketChannelTest().connectServer();
    }
}
