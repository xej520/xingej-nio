package xingej.channel;

import java.io.FileInputStream;

/**
 * 使用旧的IO 来实现从一个文件中读取内容，并将其打印出来
 * 目的是，与使用Nio的方式的区别
 */
public class OldIO {
    public static void main(String[] args) throws Exception{
        FileInputStream is = new FileInputStream("properties.properties");
        byte[] bys = new byte[1024];
        is.read(bys);
        System.out.println("----读取到的数据是-----:\t" + new String(bys));
        is.close();
    }
}
