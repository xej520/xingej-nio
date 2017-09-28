package xingej.selector.test001;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOClient {
    //通道选择器
    private Selector selector;

    public void initAndRegister(String ip, int port) throws Exception {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        selector = Selector.open();
        channel.connect(new InetSocketAddress(ip, port));
        //通道选择器 和 通道进行绑定，将该通道注册为SelectionKey,OP_CONNECT事件
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    //轮询的方式，进行监听Selector上的事件
    public void listen() throws Exception {
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }
                    //设置成 非阻塞
                    channel.configureBlocking(false);
                    //给服务器发送信息
                    channel.write(ByteBuffer.wrap(new String("hello, server!").getBytes()));
                    //在和服务器端链接成功之后，为了可以接收到服务端的消息，需要给通道设置读的权限
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    read(key);
                }
                iterator.remove();
            }
            Thread.sleep(1000);
        }
    }

    private void read(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        byte[] data = buffer.array();
        String msg = new String(data).trim();
        System.out.println("----客户端-----接收到服务器端---发送信息------:\t" + msg);
        buffer.clear();
        String msgForServer = "hello, server";
        buffer.put(msgForServer.getBytes());

        //limit()的值就是,buffer的最大值1024
        //position() 就是当前读指针的最后一个位置
        System.out.println("----client----limit()----------:\t" + buffer.limit());
        System.out.println("----client----position()-------:\t" + buffer.position());
        buffer.flip();
        //比方说，如果缓存里写入的是"hello, client"的话，那么，
        //调用flip()方法之后，
        // 就是将position()的位置，调整为0
        //limit()的值就是13，也就是说，limit()的值就是 消息的长度
        System.out.println("----client----limit()------2----:\t" + buffer.limit());
        System.out.println("----client----position()---2----:\t" + buffer.position());
        channel.write(buffer);

    }

    public static void main(String[] args) throws Exception {
        NIOClient client = new NIOClient();
        client.initAndRegister("localhost", 8081);
        client.listen();
    }
}
