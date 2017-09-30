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

        //-------------------------------测试-----put----操作---------------------------------------------------------------
        System.out.println("-----put---操作---前----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());
        //往缓冲区里，添加一个字符“h”,再查看position, limit, capacity的值
        buffer.put("h".getBytes());
        System.out.println("-----put---操作---后----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());

        //-------------------------------测试-----flip----操作---------------------------------------------------------------
        System.out.println("\n-----flip---操作---前----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());
        buffer.flip();
        System.out.println("-----flip---操作---后----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());

        //-------------------------------测试-----get----操作---------------------------------------------------------------
        System.out.println("\n-----get---操作---前----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());
        System.out.println("-----从缓存里取数据------:\t" + buffer.get());
        System.out.println("-----get---操作---后----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());


        //-------------------------------测试-----limit----操作---------------------------------------------------------------
        //重新设定缓存可以存储的容量大小是1个字节，
        //很明显，如果实际存储的字节大小，超过1个字节的话，就会抛异常的
        System.out.println("\n-----limit---操作---前----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());
        buffer.limit(1);
        try{
            //因为，缓存里已经存储里h两个字节，因此,ell是不会存储到缓存里的，而且会抛异常的
            buffer.put("ell".getBytes());
        } catch (Exception e) {
//            e.printStackTrace();
        }
        System.out.println("-----limit---操作---后----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());

        //-------------------------------测试-----clear----操作---------------------------------------------------------------
        System.out.println("\n-----clear---操作---前----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());
        buffer.clear();
        System.out.println("-----clear---操作---后----------->position:\t" + buffer.position() + "  ---->limit:\t" + buffer.limit() + "  ---->capacity:\t" + buffer.capacity());
    }

}
