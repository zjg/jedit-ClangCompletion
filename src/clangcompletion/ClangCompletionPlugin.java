
package clangcompletion;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.util.Log;

public class ClangCompletionPlugin extends EBPlugin
{
   public static final String NAME = "clangcompletion";
   public static final String OPTION = "options." + NAME + ".";
   public static final String MESSAGE = "messages." + NAME + ".";

   @Override
   public void start()
   {
      Log.log(Log.MESSAGE, this, LocalClang.getClangVersion());
   }

   @Override
   public void stop()
   {
   }

   @Override
   public void handleMessage(EBMessage message)
   {
   }
}
