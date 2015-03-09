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
        //countPerStorage();
        //removeEntry();
        countPerStorage();
        //removeBigEntry();
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
//
//
//    public static void method() {
//        Jedis jedis = new Jedis("localhost",6379,99999);
//        Set<String> keys = jedis.keys("M#67*");
//        System.out.println("keys="+keys.size());
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
//    }


    public static void countPerStorage() {
        Jedis jedis = new Jedis("localhost",6379,99999);
        Set<String> storages = jedis.smembers("STORAGE");
        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        TreeMap<Integer,Integer> mapNbt = new TreeMap<Integer,Integer>();

        for(String storage : storages) {

            Set<String> keys = jedis.keys("M#"+storage+"*");
            System.out.println(storage + " => keys="+keys.size());

            for(String key : keys) {
                //int number = jedis.hgetAll(key).size(); //NUMBER OF ENTRIES
                int number = Integer.parseInt(jedis.hget(key,"-1")); //NBT

                Integer value = mapNbt.get(number);
                if(value==null) {
                    value = 1;
                } else {
                    value++;
                }
                mapNbt.put(number,value);

                int size = jedis.hgetAll(key).size()-1;
                Integer valueCount = map.get(size);
                if(valueCount==null) {
                    valueCount = 1;
                } else {
                    valueCount++;
                }
                map.put(size,valueCount);

            }

        }
        System.out.println("*********************************");
        System.out.println("*********************************");
        System.out.println("COUNT PER SIZE:");
        System.out.println("*********************************");
        System.out.println("*********************************");
        for(Integer num : map.keySet()) {
            System.out.println(num+ "="+map.get(num));
        }
        System.out.println("*********************************");
        System.out.println("*********************************");
        System.out.println("COUNT PER NBT:");
        System.out.println("*********************************");
        System.out.println("*********************************");
        for(Integer num : mapNbt.keySet()) {
            System.out.println(num+ "="+mapNbt.get(num));
        }


    }


    public static void removeEntry() {
        Jedis jedis = new Jedis("localhost",6379,99999);
        Set<String> storages = jedis.smembers("STORAGE");
        TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
        TreeMap<Integer,Integer> mapNbt = new TreeMap<Integer,Integer>();

        for(String storage : storages) {

            Set<String> keys = jedis.keys("M#"+storage+"*");
            System.out.println(storage + " => keys="+keys.size());

            for(String key : keys) {
                //int number = jedis.hgetAll(key).size(); //NUMBER OF ENTRIES
                double nbt = Double.parseDouble(jedis.hget(key,"-1")); //NBT

                for(String subkey : jedis.hgetAll(key).keySet()) {
                    if(!subkey.equals("-1")) {
                        double nbtk = Long.parseLong(jedis.hget(key,subkey));
                        double ratio = (nbtk/nbt);
                        if(ratio<0.01d) {
                            //System.out.println("ratio:"+ratio);
                            jedis.hdel(key,subkey);
                            jedis.hincrBy(key,"-1",(long)(-nbtk));
                            if(jedis.hlen(key)<=1) {
                                //1 because nbt is store there
                                System.out.println("remove key");
                                jedis.del(key);
                            }
                        }
                    }

                }

            }

        }
//        System.out.println("*********************************");
//        System.out.println("*********************************");
//        System.out.println("COUNT PER SIZE:");
//        System.out.println("*********************************");
//        System.out.println("*********************************");
//        for(Integer num : mapNbt.keySet()) {
//            System.out.println(num+ "="+mapNbt.get(num));
//        }
//        System.out.println("*********************************");
//        System.out.println("*********************************");
//        System.out.println("COUNT PER NBT:");
//        System.out.println("*********************************");
//        System.out.println("*********************************");
//        for(Integer num : mapNbt.keySet()) {
//            System.out.println(num+ "="+mapNbt.get(num));
//        }


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