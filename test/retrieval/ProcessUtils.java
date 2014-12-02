/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import java.lang.reflect.Field;

/**
 *
 * @author lrollus
 */
public class ProcessUtils {
public static int getUnixPID(Process process) throws Exception
{
    if (process.getClass().getName().equals("java.lang.UNIXProcess"))
    {
        Class cl = process.getClass();
        Field field = cl.getDeclaredField("pid");
        field.setAccessible(true);
        Object pidObject = field.get(process);
        return (Integer) pidObject;
    } else
    {
        throw new IllegalArgumentException("Needs to be a UNIXProcess");
    }
}

public static int killUnixProcess(Process process) throws Exception
{
    int pid = getUnixPID(process);
    return Runtime.getRuntime().exec("kill -9 " + pid).waitFor();
}
}
