package xingej.selector.test002;

//基本思路逻辑：
//------------------------------------------------------------------------------
//1、创建一个通道选择器Selector
//2、创建服务器端的ServerSocketChannel通道
//      设置ServerSocketChannel属性，
//      端口号的绑定
// 3、将通道选择器 与  ServerSocketChannel通道进行绑定，并向通道选择器注册感兴趣的事件
//------------------------------------------------------------------------------
// 4、通道选择器开始工作监听管道事件，调用select()方法，死循环的方式调用
//      如果用户感兴趣的事件发生，就去处理
//      否则，就阻塞在这里

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOSelectorServer {
    //这里声明了两个缓存区，发送和接收缓冲区
    //其实，一个就可以了
    private static ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    private static ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
    private Selector selector;

    public void initAndRegister() throws Exception {
        //监听两个服务，因此需要两个端口的
        int listenPortA = 8081;
        int listenPortB = 8082;

        //创建第一个ServerSocketChannel对象实例
        ServerSocketChannel serverSocketChannelA = builderServerSocketChannel(listenPortA);
        //创建第二个ServerSocketChannel对象实例
        ServerSocketChannel serverSocketChannelB = builderServerSocketChannel(listenPortB);

        //创建通道选择器Selector
        selector = Selector.open();

        //将serverSocketChannelA 通道注册到通道选择器Selector里
        register(selector, serverSocketChannelA);
        //将serverSocketChannelB 通道注册到通道选择器Selector里
        register(selector, serverSocketChannelB);
    }

    //开始业务监听了
    public void listen() throws Exception {

        System.out.println("-----服务器-------开始接收请求-------OK--------");

        while (true) {
            int readyChannelNum = selector.select();
            if (0 == readyChannelNum) {
                continue;
            }
            //从选择器中的selectedKeys，可以获取此时已经准备好的管道事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //从迭代器移除刚选好的键
                iterator.remove();
                dealSelectionKey(selector, selectionKey);
            }

            Thread.sleep(2000);

        }
    }

    //处理具体事件
    private void dealSelectionKey(Selector selector, SelectionKey selectionKey) throws Exception {
        if (selectionKey.isAcceptable()) {

            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel clientSocketChannel = serverSocketChannel.accept();
            clientSocketChannel.configureBlocking(false);
            clientSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else //读取客户端的内容
            if (selectionKey.isReadable()) {

                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                receiveBuffer.clear();
                StringBuilder msg = new StringBuilder();
                //将客户端发送过来的数据，从管道中读取到或者说写到 接收缓存里
                while (socketChannel.read(receiveBuffer) > 0) {
                    receiveBuffer.flip();
                    msg.append(new String(receiveBuffer.array()));
                    receiveBuffer.clear();//清楚数据，下次可以重新写入
                }
                socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                //打印输出从客户端读取到的信息
                System.out.println("------>:\t" + msg.toString());

//                socketChannel.close();
            } else
                //向客户端 发送数据
                if (selectionKey.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    sendBuffer.flip();
                    socketChannel.write(sendBuffer);
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
    }

    //将ServerSocketChannel 向 Selector进行注册，也就是将两者绑定在一起，
    private void register(Selector selector, ServerSocketChannel serverSocketChannel) throws Exception {
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    //创建ServerSocketChannel对象，并进行属性设置
    private ServerSocketChannel builderServerSocketChannel(int port) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置属性，如非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(port));
        return serverSocketChannel;
    }

    public static void main(String[] args) throws Exception {
        NIOSelectorServer nioSelectorServer = new NIOSelectorServer();
        //初始化 并 注册
        nioSelectorServer.initAndRegister();
        //开始监听
        nioSelectorServer.listen();
    }
}
