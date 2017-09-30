package xingej.buffer.test001;

import java.nio.ByteBuffer;
//注意：1、原生JAVA NIO的ByteBuffer的缓冲区是不能添加字符串的，其实，从名字也可以看出来，是Byte + Buffer =>ByteBuffer
//也就是说，ByteBuffer是针对字节的缓存区
public class ByteBufferTest {

    public static void main(String[] args) {
        //分配8个字节的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(8);
        //打印出初始状态下position, limit, capacity的值
        System.out.println("---->position:\t" + buffer.position() + "\n---->limit:\t" + buffer.limit() + "\n---->capacity:\t" + buffer.capacity());
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        //往缓冲区里，添加一个字符“h”,再查看position, limit, capacity的值
        buffer.put("h2".getBytes());
        System.out.println("---->position:\t" + buffer.position() + "\n---->limit:\t" + buffer.limit() + "\n---->capacity:\t" + buffer.capacity());
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        //重新设定缓存可以存储的容量大小是2个字节，
        //很明显，如果实际存储的字节大小，超过2个字节的话，就会抛异常的
        buffer.limit(2);
        try{
            //因为，缓存里已经存储里h2两个字节，因此,ell是不会存储到缓存里的，而且会抛异常的
            buffer.put("ell".getBytes());
        } catch (Exception e) {

        }
        System.out.println("---->position:\t" + buffer.position() + "\n---->limit:\t" + buffer.limit() + "\n---->capacity:\t" + buffer.capacity());

        buffer.flip();
        System.out.println("----->:\t" + new String(buffer.array()));
    }

}
