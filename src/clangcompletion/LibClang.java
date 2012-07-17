
package clangcompletion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

public class LibClang
{
   static public String getClangVersion()
   {
      String version = "unknown";
      try {
         Process p = new ProcessBuilder("clang", "-v").redirectErrorStream(true).start();
         BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
         version = stdOut.readLine();
         int rval = p.waitFor();
         if (rval != 0)
         {
            Log.log(Log.ERROR, null, "Clang process returned: " + rval);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      
      return version;
   }

   static public void startup()
   {
   }
   
   static public void shutdown()
   {
   }

   static public boolean setCurrentFile(String filePath)
   {
      return false;
   }
   
   static public String getCursorType(int fileOffset)
   {
      return "<none>";
   }

   static public String[] getCompletions(int line, int column)
   {
      return new String[0];
   }
}
