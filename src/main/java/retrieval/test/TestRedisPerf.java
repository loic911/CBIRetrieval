package retrieval.test;

import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.TreeMap;

/**
 * Created by lrollus on 11/02/15.
 */
public class TestRedisPerf {

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        removeBigEntry();
        System.out.println(System.currentTimeMillis()-start);
    }


    public static void removeBigEntry() {
        Jedis jedis = new Jedis("localhost",6379,2000);
        Set<String> keys = jedis.keys("M#*");
        System.out.println("keys="+keys.size());
        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        TreeMap<Integer,Integer> mapNbt = new TreeMap<Integer,Integer>();
        for(String key : keys) {
            int number = jedis.hgetAll(key).size(); //NUMBER OF ENTRIES
            if(number>100) {
                jedis.del(key);
            }
        }
    }


    public static void compress() {
        Jedis jedis = new Jedis("localhost",6379,2000);
        Set<String> keys = jedis.keys("COMPRESS#*");
        System.out.println("keys="+keys.size());
//        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
//        TreeMap<Integer,Integer> mapNbt = new TreeMap<Integer,Integer>();
//        for(String key : keys) {
//            //int number = jedis.hgetAll(key).size(); //NUMBER OF ENTRIES
//            int number = Integer.parseInt(jedis.hget(key,"-1")); //NBT
//            Integer value = map.get(number);
//            if(value==null) {
//                value = 1;
//            } else {
//                value++;
//            }
//            map.put(number,value);
//        }
//
//        for(Integer num : map.keySet()) {
//            System.out.println(num+ "="+map.get(num));
//        }
    }


    public static void method() {
        Jedis jedis = new Jedis("localhost",6379,2000);
        Set<String> keys = jedis.keys("M#*");
        System.out.println("keys="+keys.size());
        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        TreeMap<Integer,Integer> mapNbt = new TreeMap<Integer,Integer>();
        for(String key : keys) {
            //int number = jedis.hgetAll(key).size(); //NUMBER OF ENTRIES
            int number = Integer.parseInt(jedis.hget(key,"-1")); //NBT
            Integer value = map.get(number);
            if(value==null) {
                value = 1;
            } else {
                value++;
            }
            map.put(number,value);
        }

        for(Integer num : map.keySet()) {
            System.out.println(num+ "="+map.get(num));
        }
    }


//    public static void method1(int max) {
//        Jedis jedis = new Jedis("localhost",6379,2000);
//        for(int i=0;i<max;i++) {
//            for(int j=0;j<8;j++) {
//                jedis.hset(i+"",j+"","value");
//                jedis.hget(i+"",j+"");
//            }
//            jedis.hgetAll(i+"");
//        }
//    }
//
//
//    public static void method2(int max) {
//
//        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost",6379);
//        for(int i=0;i<max;i++) {
//            for(int j=0;j<8;j++) {
//                try (Jedis jedis = pool.getResource()) {
//                    jedis.hset(i + "", j + "", "value");
//                }
//                try (Jedis redis = pool.getResource()) {
//                redis.hget(i + "", j + "");
//                }
//            }
//            try (Jedis jedis = pool.getResource()) {
//            jedis.hgetAll(i+"");
//            }
//        }
//    }
}
//
//
///// Jedis implements Closable. Hence, the jedis instance will be auto-closed after the last statement.
//try (Jedis jedis = pool.getResource()) {
//        /// ... do stuff here ... for example
//        jedis.set("foo", "bar");
//        String foobar = jedis.get("foo");
//        jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike");
//        Set<String> sose = jedis.zrange("sose", 0, -1);
//        }
///// ... when closing your application:
//        pool.destroy();