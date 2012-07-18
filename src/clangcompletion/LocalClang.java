
package clangcompletion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.gjt.sp.util.Log;

import completion.service.CompletionCandidate;

public class LocalClang
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

   static public List<CompletionCandidate> getCompletions(String filePath, int line, int column)
   {
      List<String> cmdLine = new ArrayList<String>();
      cmdLine.add("clang");
      cmdLine.add("-cc1");
      cmdLine.add("-fsyntax-only");
      
      // add include dirs
      for (String includeDir : GeneralOptionsPane.getIncludeDirs())
      {
         if (includeDir != null && !includeDir.trim().isEmpty())
         {
            cmdLine.add("-I" + includeDir);
         }
      }
      
      cmdLine.add("-code-completion-at");
      cmdLine.add(filePath + ":" + line + ":" + column);
      cmdLine.add(filePath);
      
      Log.log(Log.DEBUG, null, cmdLine);
      
      List<String> results = new ArrayList<String>();
      try {
         Process p = new ProcessBuilder(cmdLine).start();
         
         BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String stdOutLine = stdOut.readLine();
         while (stdOutLine != null)
         {
            // System.err.println(stdOutLine);
            results.add(stdOutLine);
            stdOutLine = stdOut.readLine();
         }
         
         // BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
         // String stdErrLine = stdErr.readLine();
         // while (stdErrLine != null)
         // {
         //    System.err.println(stdErrLine);
         //    stdErrLine = stdErr.readLine();
         // }
         
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
      
      List<CompletionCandidate> codeCompletions = new ArrayList<CompletionCandidate>();
      for (String result : results)
      {
         CompletionCandidate candidate = ClangCompletionUtils.parseCommandLineCompletion(result);
         if (candidate != null)
         {
            codeCompletions.add(candidate);
         }
      }
      return codeCompletions;
   }
}
