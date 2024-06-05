package fr.yronusa.llmcraft;




import org.python.util.PythonInterpreter;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PythonIntegration {

    public static void test() throws IOException {
        JarFile jar = new JarFile("/home/yro/Spigot/1.12/plugins/LLM-craft.jar");
        JarEntry entry = jar.getJarEntry("hello.py");
        InputStream is = jar.getInputStream(entry);

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(is);
        is.close();

    }
}
