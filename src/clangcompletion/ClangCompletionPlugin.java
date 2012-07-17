
package clangcompletion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PositionChanging;
import org.gjt.sp.util.Log;

import clangcompletion.LibClang;

public class ClangCompletionPlugin extends EBPlugin
{
   public static final String NAME = "ClangCompletion";
   public static final String OPTION = "options.ClangCompletion.";
   public static final String MESSAGE = "messages.ClangCompletion.";

   @Override
   public void start()
   {
      Log.log(Log.DEBUG, this, LibClang.getClangVersion());
      LibClang.startup();
   }

   @Override
   public void stop()
   {
      LibClang.shutdown();
   }

   @Override
   public void handleMessage(EBMessage message)
   {
      if (message instanceof EditPaneUpdate)
      {
         EditPaneUpdate paneUpdate = (EditPaneUpdate)message;
         if (paneUpdate.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
         {
            EditPane pane = paneUpdate.getEditPane();
            Buffer buf = pane.getBuffer();

            if ((buf.getMode() == jEdit.getMode("c++"))
                || (buf.getMode() == jEdit.getMode("c")))
            {
               boolean parsedOkay = LibClang.setCurrentFile(buf.getPath());
   
               if (parsedOkay)
                  Log.log(Log.DEBUG, this, "parsed " + buf.getPath());
               else
                  Log.log(Log.DEBUG, this, "failed to parse " + buf.getPath());
            }
         }
      }
   }
}
