package xingej.selector.test002;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class NIOClient2 {
    public static void main(String[] args) throws Exception {

        String msg = "hello, NIO Server, I'm ";

        int[] ports = {8081, 8082};
        for (int i = 0; i < 10; i++) {
            int index = i % 2;
            int port = ports[index];
            new Thread(new SocketChannelThread(msg + i +" client", port)).start();
        }
    }
}

class SocketChannelThread implements Runnable {
    //向服务器发送的消息体
    private String msg;
    private int port;

    private SocketChannel clientChannel;

    public SocketChannelThread(String msg, int port) {
        this.msg = msg;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            //创建一个SocketChannel对象实例
            clientChannel = SocketChannel.open();
            //链接服务器
            clientChannel.connect(new InetSocketAddress("localhost", port));
            //设置通道未非阻塞模式
            clientChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int sendNum = new Random().nextInt(5) + 1;
            for(int i = 0; i < sendNum; i++) {
                buffer.put(new String(msg).getBytes());
                buffer.flip();
                //将缓冲区的内容发送到通道里
                clientChannel.write(buffer);
                //清理缓存区，下次重新写入
                buffer.clear();
                //每次发送完成后，休息几秒中，就是为了测试
                Thread.sleep(sendNum * 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                //如果此通过处于开通状态的话，就关闭此通道
               if (clientChannel.isOpen()) {
                   System.out.println("-----关闭通道了------");
                   clientChannel.close();
               }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
