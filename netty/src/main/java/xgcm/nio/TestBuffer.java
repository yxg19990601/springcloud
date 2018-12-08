package xgcm.nio;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.channels.FileChannel.MapMode;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * buffer 负责数据的存取（底层就是数据） 用于存储不同数据类型的数据
 *
 * 数据类型不同，就会有不同类型的缓冲区（boolean 除外）
 *
 * ByteBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * CharBuffer
 *
 *上述缓冲区都是通过一个方式获取 allocate() 获取缓冲区
 *存取数据的2个核心方法：
 * put ： 存入数据到缓冲区中
 * get ： 获取缓冲区中的数据
 *
 *     private int mark = -1;
 *     private int position = 0; ， 位置，表示缓冲区正在操作数据的位置，
 *     private int limit;   界限，表示缓冲区可以操作数据的大小，limit 后边的数据不能进行读写
 *     private int capacity;  容量，表示缓冲区最大数据的容量，声明后无法改变
 *
 *     Invariants: mark <= position <= limit <= capacity
 *
 * mark 标记当前position 的位置
 * reset() 可以回到mark 的位置
 *
 *
 * 非直接缓冲区
 * allocate() 方法分配的缓冲区， 将建立在JVM 的内存中
 *
 * 直接缓冲区
 * allocateDirect() 方法建立的缓冲区，将缓冲区建立在物理内存中,可以提高效率
 *
 * 判断是否直接缓冲区
 * isDirect() 返回true 则是直接缓冲区
 *
 * 获取 Channel 的方法
 * 1. FileInputStream.getChannel
 * 2. FileChannel.open()
 *
 * copy 文件
 * transferTo
 *
 *
 *
 * @Author YXG
 * @Date 2018-11-15 21:29
 */
public class TestBuffer {



    //编码和解码
    @Test
    public void test7() throws Exception {
        Charset charset = Charset.forName("UTF-8");
        // 获取编码器和解码器
        CharsetDecoder charsetDecoder = charset.newDecoder();
        CharsetEncoder charsetEncoder = charset.newEncoder();

        CharBuffer allocate = CharBuffer.allocate(1024);
        CharBuffer append = allocate.append("我是世界上最聪明的人！");

        append.flip();
        ByteBuffer encode = charsetEncoder.encode(allocate);

        System.out.println(new String(encode.array(),"UTF-8"));


    }


    // CharSet
    @Test
    public void test6() {
        // 查看nio 支持的字符集
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();

        Set<Map.Entry<String, Charset>> entries = stringCharsetSortedMap.entrySet();
        for (Map.Entry<String, Charset> entry : entries) {

            System.out.println("entry.getKey() +\" \"+entry.getValue() = " + entry.getKey() +" "+entry.getValue());
            
        }



    }
    // 通道之间的数据传输
    @Test
    public void test5() throws  Exception{
        long l = System.currentTimeMillis();
        FileChannel inChannel = FileChannel.open(Paths.get("F:/all.zip"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("E:/all.zip"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
        inChannel.transferTo(0,inChannel.size(),outChannel);
        inChannel.close();
        outChannel.close();

        System.out.println(System.currentTimeMillis()- l);
    }

    // 内存文件映射， 直接缓冲区文件copy
    @Test
    public void test4() throws  Exception{
        long l = System.currentTimeMillis();
        FileChannel inChannel = FileChannel.open(Paths.get("F:/all.zip"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("E:/all.zip"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);

        // 内存映射文件
        MappedByteBuffer inMap = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMap = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
        // 直接对缓冲区进行读写操作
        byte[] bytes = new byte[inMap.limit()];
        inMap.get(bytes);
        outMap.put(bytes);
        inChannel.close();
        outChannel.close();

        System.out.println(System.currentTimeMillis()- l);
    }

    // 原生BIO 文件复制
    @Test
    public void test3() throws  Exception{
        int m = 1;
        long start = System.currentTimeMillis();
        FileInputStream inputStream = new FileInputStream("F:/all.zip");
        FileOutputStream outputStream = new FileOutputStream("E:/all.zip");
        byte[] bytes = new byte[1024*1024];


        int len = -1;
        while ((len = inputStream.read(bytes)) != -1) {
            System.out.println(m+"m" );
            outputStream.write(bytes);
            m++;
        }

        inputStream.close();
        outputStream.close();
        System.out.println(System.currentTimeMillis() - start);
    }

    // nio 文件coyp ， 非直接缓冲区
    @Test
    public void test2() throws  Exception{
        int m = 1;
        long start = System.currentTimeMillis();
        FileInputStream inputStream = new FileInputStream("F:/all.zip");
        FileOutputStream outputStream = new FileOutputStream("E:/all.zip");
        FileChannel channel = inputStream.getChannel();
        FileChannel channel1 = outputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024 );
        while (channel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            channel1.write(byteBuffer);
            byteBuffer.clear();
            System.out.println(m+"m" );
            m++;
        }
        channel.close();
        channel1.close();
        if (inputStream != null) {
            inputStream.close();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test() {
        // 分配一个缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        byteBuffer.put((byte) 15);
        byteBuffer.put((byte) 20);
        System.out.println(byteBuffer.position());
        byteBuffer.putInt(456);
        System.out.println(byteBuffer.position());
        byteBuffer.putLong(5456456l);
        System.out.println(byteBuffer.position());
        byteBuffer.put("我是杨旭光".getBytes());
        System.out.println(byteBuffer.position());

        System.out.println(byteBuffer.get(0));

        // 切换到读模式
        byteBuffer.flip();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());


        // 重读数据
        byteBuffer.rewind();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());


        // 清空缓冲区(但是数据依然存在)
        byteBuffer.clear();

        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println(byteBuffer.get(1));

    }
}
