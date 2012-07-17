
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
      
      // todo - don't use the system loader, we need to use a custom classloader
      // in order to be able to unload the shared library when the plugin is unloaded
      System.load(pluginPath + "/libClangCompletionPluginLibClang.so");
   }

   static public native String getClangVersion();

   static public native void startup();
   static public native void shutdown();

   static public native boolean setCurrentFile(String filePath);
   static public native String getCursorType(int fileOffset);
   
   static public native String[] getCompletions(int line, int column);
}
