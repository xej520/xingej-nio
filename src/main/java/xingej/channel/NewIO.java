package xingej.channel;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用NIO来读取文件里的数据
 */
public class NewIO {
    public static void main(String[] args) throws Exception{
        FileInputStream is = new FileInputStream("properties.properties");
        //为该文件输入流 ， 生成唯一的文件通道FileChannel
        FileChannel fileChannel = is.getChannel();
        //开辟一个长度为1024的字节缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        fileChannel.read(buffer);
        System.out.println("---读取到的数据----:\t" + new String(buffer.array()));
        System.out.println("---->" + buffer.isDirect() + ", " + buffer.isReadOnly());
        fileChannel.close();
        is.close();

    }
}
