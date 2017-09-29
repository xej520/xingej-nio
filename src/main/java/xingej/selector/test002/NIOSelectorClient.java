package xingej.selector.test002;
//创建SocketChannel
//      链接服务器
//向服务器发送消息

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//改成多线程模式
public class NIOSelectorClient {
    private static Selector selector;
    private SocketChannel socketChannel;
    private SocketChannel socketChannelB;
    private SocketChannel socketChannelC;

    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

    public void initAndRegister() throws Exception{
        selector = Selector.open();
        createAndRegister(5);
    }

    public void initAndRegister3() throws Exception{
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 8081));

        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        socketChannelB = SocketChannel.open();
        socketChannelB.configureBlocking(false);
        socketChannelB.connect(new InetSocketAddress("localhost", 8082));
        socketChannelB.register(selector, SelectionKey.OP_CONNECT);

        socketChannelC = SocketChannel.open();
        socketChannelC.configureBlocking(false);
        socketChannelC.connect(new InetSocketAddress("localhost", 8082));
        socketChannelC.register(selector, SelectionKey.OP_CONNECT);
    }


    private void createAndRegister(int socketChannelNum) throws Exception{
        ExecutorService socketThreadPool = Executors.newFixedThreadPool(5);
        Integer[] ports = {8081, 8082};
        for(int i = 0; i < socketChannelNum; i++) {
            int port = ports[i % 2];
           socketThreadPool.submit(new SocketChannelThread(port));
        }
        socketThreadPool.shutdown();
    }

    class SocketChannelThread implements Runnable{
        private int port;
        public SocketChannelThread(int port) {
            this.port = port;
        }
        @Override
        public void run() {
            try {
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                System.out.println("--------1-------");
                //1到10秒钟，随机休息
                int time = (new Random().nextInt(10) + 1) * 1000;
                System.out.println("-----time------:\t" + time);
                Thread.sleep(time);
                System.out.println("--------2-------port:\t" + port);
                sc.connect(new InetSocketAddress("localhost", port));
                System.out.println("--------3-------");
                sc.register(selector, SelectionKey.OP_CONNECT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws Exception{

//        Thread.sleep(15000);

        while (true) {
            System.out.println("-----客户端----准备好了----:\t");
            int readyChannelNum = selector.select();

            System.out.println("-----客户端----准备好的管道数量是-----:\t" + readyChannelNum);
            if (0 == readyChannelNum) {
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                iterator.remove();
                if (selectionKey.isConnectable()) {
                    if (socketChannel.isConnectionPending()) {
                        socketChannel.finishConnect();
                        System.out.println("----客户端----链接完毕了-----");
                    }
                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                }else if (selectionKey.isWritable()) {
                    sendBuffer.clear();
                    sendBuffer.put("hello, server, I'm client! Are you OK!!!".getBytes());
                    //flip()必须有的
                    sendBuffer.flip();
                    socketChannel.write(sendBuffer);
                    System.out.println("----客户端---向服务器---发送消息-----完毕----OK-----");
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }
            }
            Thread.sleep(500);
        }

    }

    public static void main(String[] args) throws Exception{
        NIOSelectorClient nioSelectorClient = new NIOSelectorClient();
        nioSelectorClient.initAndRegister();
        nioSelectorClient.listen();
    }
}
