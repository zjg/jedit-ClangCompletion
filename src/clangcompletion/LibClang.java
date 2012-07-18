
package clangcompletion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

public class LibClang
{
   private static String currentFile;
   
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
      currentFile = filePath;
      return true;
   }
   
   static public String getCursorType(int fileOffset)
   {
      return "<none>";
   }

   static public List<String> getCompletions(int line, int column)
   {
      if (currentFile == null)
      {
         return null;
      }
      
      List<String> results = new ArrayList<String>();
      
      List<String> cmdLine = new ArrayList<String>();
      cmdLine.add("clang");
      cmdLine.add("-cc1");
      cmdLine.add("-fsyntax-only");
      cmdLine.add("-fno-caret-diagnostics");
      cmdLine.add("-fdiagnostics-print-source-range-info");
      cmdLine.add("-code-completion-at");
      cmdLine.add(currentFile + ":" + line + ":" + column);
      cmdLine.add(currentFile);
      
      Log.log(Log.DEBUG, null, cmdLine);
      
      try {
         Process p = new ProcessBuilder(cmdLine).redirectErrorStream(true).start();
         BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String stdOutLine = stdOut.readLine();
         while (stdOutLine != null)
         {
            System.err.println(stdOutLine);
            
            if (stdOutLine.startsWith("COMPLETION:"))
            {
               results.add(stdOutLine.replaceAll("COMPLETION: (.*) : .*", "$1"));
            }
            
            stdOutLine = stdOut.readLine();
         }
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
      
      return results;
   }
}
