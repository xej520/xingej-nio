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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//改成多线程模式
public class NIOSelectorClient {
    private static Selector selector;
    private static boolean flag = false;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

    public void initAndRegister() throws Exception{
        selector = Selector.open();
        createAndRegister(5);
    }

    private void createAndRegister(int socketChannelNum) throws Exception{
        ExecutorService socketThreadPool = Executors.newFixedThreadPool(5);
        CountDownLatch _latchs = new CountDownLatch(socketChannelNum);
        Integer[] ports = {8081, 8082};

        for(int i = 0; i < socketChannelNum; i++) {
            int port = ports[i % 2];
           socketThreadPool.submit(new SocketChannelThread(port, _latchs));
        }
        _latchs.await();
        socketThreadPool.shutdown();
        flag = true;

    }

    class SocketChannelThread implements Runnable{
        private CountDownLatch _latch;
        private int port;
        private SocketChannel socketChannel;

        public SocketChannelThread(int port, CountDownLatch _latch) {
            this.port = port;
            this._latch = _latch;
        }
        @Override
        public void run() {
            try {
                socketChannel= SocketChannel.open();
                socketChannel.configureBlocking(false);
                //1到10秒钟，随机休息
                //这里，添加时间的目的，是想模拟一下，不想同一时间，向服务器发起请求
                int time = (new Random().nextInt(10) + 1) * 1000;
                System.out.println("----此通道----休息的时间是------:\t" + time / 1000 + " 秒");
                Thread.sleep(time);
                System.out.println("--------2-------port:\t" + port);
                socketChannel.connect(new InetSocketAddress("localhost", port));
                System.out.println("--------3-------");
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //计数器，减一
                _latch.countDown();
            }
        }
    }

    public void listen() throws Exception{

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
                    //这里注册的事件是write，
                    //效果就是，客户端不断的发送消息
                    //当然，也可以修改成其他事件，如SelectionKey.OP_READ
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }

            }
            //每隔1秒中，就向服务器发送信息
            Thread.sleep(1000);
        }

    }

    public static void main(String[] args) throws Exception{
        NIOSelectorClient nioSelectorClient = new NIOSelectorClient();
        nioSelectorClient.initAndRegister();

        //死循环的方式，来监听标志位，
        //一旦标志位发生改变，就开始监听
        while (true) {
            if (flag) {
                nioSelectorClient.listen();
                break;
            }
        }
    }
}
