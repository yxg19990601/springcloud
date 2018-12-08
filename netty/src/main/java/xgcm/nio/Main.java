package xgcm.nio;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * @Author YXG
 * @Date 2018-11-15 21:16
 */
public class Main {
    public static void main(String[] args) throws  Exception{

        FileInputStream inputStream = new FileInputStream("E:\\vedio\\nio.zip");
        Channel channel = inputStream.getChannel();
        ByteBuffer in = ByteBuffer.allocate(1024);

        ((FileChannel) channel).read(in);



        for (byte i : in.array()) {
            System.out.println("i = " + i);
        }
    }
}
