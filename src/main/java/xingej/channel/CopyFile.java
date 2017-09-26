package xingej.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 综合性的测试用例，主要用来copy文件，
 * 使用NIO的主要步骤，
 * 读的流程
 * 1、从输入流中获取管道，将管道的数据读到缓存，然后再对缓存进行操作
 * 写的流程
 * 1、从输出流中获取管道，将
 */
public class CopyFile {
    public static void main(String[] args) throws Exception {
        String inFile = "gitHub.txt";
        String outFile = "gitHub2.txt";
        //获取源文件和目标文件的输入流、输出流
        FileInputStream fin = new FileInputStream(inFile);
        FileOutputStream fout = new FileOutputStream(outFile);
        //获取输入、输出通道
        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();
        //创建缓存区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (true) {
            //清楚缓存区的数据，可以接收新的数据
            buffer.clear();
            //从输入通道中将数据读到缓冲区
            int r = fcin.read(buffer);
            //read方法返回读取的字节数，可能为0， 如果该通道已经达到流的末尾，则返回-1
            if (r == -1) {
                break;
            }
            //flip方法，让缓冲区可以将新读入的数据写入另一个通道
            buffer.flip();
            //从缓存区 将数据写到 输出通道里
            fcout.write(buffer);
        }
        fcin.close();
        fout.close();
    }
}
