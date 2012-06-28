
package clangcompletion;

// import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

public class LibClang
{
   static
   {
      String root = jEdit.getSettingsDirectory();
      if (root == null)
      {
         root = jEdit.getJEditHome();
      }
      String pluginPath = root + "/jars";

      // String libPath = System.getProperty("java.library.path");
      // if (libPath.length() > 0)
      // {
      //    libPath += ":";
      // }
      // libPath += pluginPath;
      // System.setProperty("java.library.path", libPath);

      // Log.log(Log.DEBUG, null, "java.library.path: " + System.getProperty("java.library.path"));
      // System.loadLibrary("ClangCompletionPluginLibClang");

      System.load(pluginPath + "/libClangCompletionPluginLibClang.so");
   }

   static public native String getClangVersion();

   static public native void startup();
   static public native void shutdown();

   static public native boolean setCurrentFile(String filePath);
}
