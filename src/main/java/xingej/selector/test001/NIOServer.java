package xingej.selector.test001;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    //声明一个NIO通道选择器
    private Selector selector;

    public void initAndRegister(int port) throws Exception{
        //获得一个ServerSocketChannel对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //将此通道设置为 非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(port));

        //初始化NIO通道选择器
        this.selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT );
        //下面的写法是错误的，
        //
//        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public void listen() throws Exception{
        System.out.println("------服务器----已经准备好-----开始接收----客户端请求--------");

        while (true) {
            //如果有新来的注册事件发生，不会阻塞，
            //否则阻塞在这里
            selector.select();
            //返回键集集合，暗含的信息是 已经准备好的通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //从迭代器中，移除/删除此key，下次就不会在对此key进行处理了

                //服务端接收到 链接请求事件
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
//                    server.configureBlocking(false);
                    //获得与客户端连接的通道
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);
                    //在客户端链接成功后，为了可以接收到客户端的信息，需要给通道设置读的权限
                    channel.register(this.selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    read(selectionKey);
                }
                iterator.remove();
            }
        }
    }

    //业务处理，客户端发送过来的消息
    private void read(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();

        //创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //将数据由SocketChannel管道 读取到 缓冲区里
        channel.read(buffer);
        //将缓冲区的数据，转换成 字节数据
        byte[] bytes = buffer.array();
        String msg = new String(bytes).trim();
        System.out.println("-----接收到----客户端----发送过来的信息------:\t" + msg);
        buffer.clear();

        //收到消息后，给客户端发送消息
        String msgForClient = "hello, client";
        buffer.put(msgForClient.getBytes());
        buffer.flip();
        //将数据从缓存区 再写到 SocketChannel通道里
        channel.write(buffer);

    }

    public static void main(String[] args) throws Exception{
        NIOServer server = new NIOServer();
        server.initAndRegister(8081);
        server.listen();
    }
}
