package xingej.test001;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户端
 */
public class ClientDriver {
    public void initSocketChannel() throws Exception {
        ExecutorService socketChannelPool = Executors.newFixedThreadPool(10);

        //10个线程，公用1个缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        for (int i = 0; i < 10; i++) {

            SocketChannel socketChannel = SocketChannel.open();

            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8081));

            //跟服务器链接成功后，才进行其他操作
            if (socketChannel.isConnected()) {
                byteBuffer.clear();
                byteBuffer.put(new String("-----客户端[" + i + "]发起请求---hello java NIO---").getBytes());
                byteBuffer.flip();
                socketChannelPool.submit(new SocketChannelThread(socketChannel, byteBuffer));
                //清理掉缓存里的内容，等待下一次的重新写入
            }
        }

        socketChannelPool.shutdown();
    }

    class SocketChannelThread implements Runnable {

        private SocketChannel socketChannel;

        private ByteBuffer byteBuffer;

        public SocketChannelThread(SocketChannel socketChannel, ByteBuffer byteBuffer) {
            this.socketChannel = socketChannel;
            this.byteBuffer = byteBuffer;
        }

        @Override
        public void run() {
            try {
                //业务很简单，只负责将缓存里的数据，写到管道里
                socketChannel.write(byteBuffer);

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

    public static void main(String[] args) throws Exception{

        new ClientDriver().initSocketChannel();
    }

}
