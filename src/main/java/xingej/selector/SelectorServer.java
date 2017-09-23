package xingej.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 学习NIO中selector知识
 */
public class SelectorServer {

    public static ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    public static ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

    public static void startServer() {
        //创建两个Channel通过，因此，需要监听两个端口
        int listenPortA = 8082;
        int listenPortB = 8084;
        //往发送缓存区里，扔进一些信息
        sendBuffer.put("message from server".getBytes());
        try {
            //创建serverchannel, 绑定对应的端口
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(listenPortA);
            serverSocket.bind(inetSocketAddress);

            //创建serverchannel, 绑定对应的端口
            ServerSocketChannel serverSocketChannel1 = ServerSocketChannel.open();
            ServerSocket serverSocket1 = serverSocketChannel1.socket();
            InetSocketAddress inetSocketAddress1 = new InetSocketAddress(listenPortB);
            serverSocket1.bind(inetSocketAddress1);

            //创建selector对象
            Selector selector = Selector.open();
            //将第一个channel 注册到selector中
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //将第二个channel 注册到selector中
            serverSocketChannel1.configureBlocking(false);
            serverSocketChannel1.register(selector, SelectionKey.OP_ACCEPT);

            //监听端口
            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    dealSelectionKey(selector, selectionKey);
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dealSelectionKey(Selector selector, SelectionKey selectionKey) {
        try {
            //接收新连接
            if (selectionKey.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                clientSocketChannel.configureBlocking(false);
                clientSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                System.out.println("channel is ready acceptable");
            } else if (selectionKey.isReadable()) {
                selectionKey.channel().register(selector, SelectionKey.OP_READ);
                System.out.println("channel is connectable");
            } else if (selectionKey.isReadable()) {
                //读取客户端的内容
                SocketChannel clientSocketChannel = (SocketChannel) selectionKey.channel();
                receiveBuffer.clear();
                clientSocketChannel.read(receiveBuffer);
                selectionKey.interestOps(SelectionKey.OP_READ);
                System.out.println("message from client is:\t" + new String(receiveBuffer.array()));
            } else if (selectionKey.isWritable()) {
                //向客户端写数据
                SocketChannel clientSocketChannel = (SocketChannel) selectionKey.channel();
                sendBuffer.flip();
                System.out.println("sendBuffer = " + new String(sendBuffer.array()));
                clientSocketChannel.write(sendBuffer);
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                System.out.println("channel is writable.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startServer();
    }

}
