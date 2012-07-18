
package clangcompletion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

import completion.service.CompletionCandidate;

public class LocalClang
{
   static public String getClangVersion()
   {
      String version = "unknown";
      try {
         long start = System.currentTimeMillis();
         Process p = new ProcessBuilder("clang", "-v").redirectErrorStream(true).start();
         BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
         version = stdOut.readLine();
         int rval = p.waitFor();
         if (rval != 0)
         {
            Log.log(Log.ERROR, null, "Clang process returned: " + rval);
         }
         long end = System.currentTimeMillis();
         Log.log(Log.MESSAGE, null, "Clang process took " + (end-start) + "ms");
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      
      return version;
   }

   static public List<CompletionCandidate> getCompletions(String filePath, int line, int column)
   {
      File workingDir = null;
      List<String> cmdLine = new ArrayList<String>();
      cmdLine.add("clang");
      // cmdLine.add("-v");
      cmdLine.add("-fsyntax-only");
      cmdLine.add("-Xclang");
      cmdLine.add("-code-completion-at=" + filePath + ":" + line + ":" + column);
      
      switch (GeneralOptionsPane.getCommandLineSource())
      {
         case MANUAL_INCLUDE_PATHS:
         {
            // add include dirs
            for (String includeDir : ManualIncludeOptionsPane.getIncludeDirs())
            {
               cmdLine.add("-I" + includeDir);
            }
            
            // add the file
            cmdLine.add(filePath);
            
            break;
         }
         case BUILD_COMMAND_LINE_FILES:
         {
            // look for the build command line file
            String path = MiscUtilities.getParentOfPath(filePath);
            String filename = MiscUtilities.getFileName(filePath);
            String buildCmdFile = path + "." + filename + ".build_cmd";
            
            String buildWorkingDir = path;
            String buildCommand = "";
            
            try {
               BufferedReader fileReader = new BufferedReader(new FileReader(buildCmdFile));
               buildWorkingDir = fileReader.readLine();
               buildCommand = fileReader.readLine();
            } catch (IOException e) {
               e.printStackTrace();
               return null;
            }
            
            if (buildWorkingDir == null || buildCommand == null)
            {
               Log.log(Log.ERROR, null, "Failed to read build command file: " + buildCmdFile);
               return null;
            }
            
            workingDir = new File(buildWorkingDir);
            
            // todo - should parse the command line with as actual command line parser,
            //        since this currently doesn't handle quoted strings with spaces
            List<String> buildCmdLine = new ArrayList<String>(Arrays.asList(buildCommand.split(" ")));
            buildCmdLine.remove(0);  // remove the compiler binary
            cmdLine.addAll(buildCmdLine);
            
            break;
         }
      }
      
      Log.log(Log.DEBUG, null, cmdLine);
      
      List<String> results = new ArrayList<String>();
      try {
         long start = System.currentTimeMillis();
         Process p = new ProcessBuilder(cmdLine).directory(workingDir).start();
         
         BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String stdOutLine;
         while ((stdOutLine = stdOut.readLine()) != null)
         {
            // System.err.println(stdOutLine);
            results.add(stdOutLine);
         }
         
         BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
         String stdErrLine;
         while ((stdErrLine = stdErr.readLine()) != null)
         {
            System.err.println(stdErrLine);
         }
         
         int rval = p.waitFor();
         if (rval != 0)
         {
            Log.log(Log.ERROR, null, "Clang process returned: " + rval);
         }
         long end = System.currentTimeMillis();
         Log.log(Log.MESSAGE, null, "Clang process took " + (end-start) + "ms");
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
