package xgcm.nio;

import java.nio.channels.DatagramChannel;

/**
 * @Author YXG
 * @Date 2018-12-16 9:39
 */
public class UdpNio {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 1; i <= 1000000; i++) {
            System.out.println("徐芳我爱你 : "+i);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
