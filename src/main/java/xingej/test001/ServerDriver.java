package xingej.test001;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * 服务器端
 */
public class ServerDriver {

    private static ServerSocketChannel serverSocketChannel;
    private static ThreadPoolExecutor socketChannelPool;

    // 静态块，一加载ServerDriver类，就会创建ServerSocketChannel对象，
    // 并进行 端口的绑定工作
    static {
        //在服务器端，创建一个ServerSocketChannel对象
        //作用，监听客户端的tcp连接，并创建SocketChannel连接对象
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8081));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建线程池
        //设置线程池的属性为：至少存在1个线程，最大10个线程，
        //如果客户端数量超过10个后，新的客户端请求会暂时放到
        //阻塞队列里，阻塞队列的大小是100个
        socketChannelPool = new ThreadPoolExecutor(1, 10, 1000, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100));

    }

    public void startupServer() throws Exception {
        System.out.println("--------服务器开始工作--------监听8081端口的tcp连接请求-------");

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();

            synchronized (ServerDriver.class) {
                if (null != socketChannel) {
//                InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
//                System.out.print("-----接收到客户端[" + remoteAddress.getHostName() + ":" + remoteAddress.getPort() + "]的连接请求------开始处理-----:\t");
                    //服务器只负责接收，客户端的请求
                    //具体的业务逻辑 抛给工作线程去负责
                    socketChannelPool.submit(new ServerSocketChannelThread(socketChannel, 1024));
                }
//            Thread.sleep(1000);
            }
        }

    }

    class ServerSocketChannelThread implements Runnable {
        private SocketChannel socketChannel;
        private ByteBuffer byteBuffer;

        public ServerSocketChannelThread(SocketChannel socketChannel, int byteBufferSize) {
            this.socketChannel = socketChannel;
            byteBuffer = ByteBuffer.allocate(byteBufferSize);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int size = socketChannel.read(byteBuffer);

                    if (-1 == size) {
                        break;
                    }
                    System.out.println(new String(byteBuffer.array()));

                    byteBuffer.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //先加载静态代码块，
        //然后才会创建对象，也就是调用ServerDriver的构造方法，
        new ServerDriver().startupServer();
    }
}



