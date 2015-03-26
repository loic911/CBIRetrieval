/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval;

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
