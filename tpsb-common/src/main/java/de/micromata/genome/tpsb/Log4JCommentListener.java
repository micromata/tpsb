package de.micromata.genome.tpsb;

import org.apache.log4j.Logger;

/**
 * Logs the addComment to log4j as info
 * 
 * @author roger
 * 
 */
public class Log4JCommentListener extends CommonCommentListener
{
  public static final Logger log = Logger.getLogger(Log4JCommentListener.class);

  @Override
  public void addCommentText(TestBuilder< ? > builder, String text)
  {
    log.info(text);
  }

}
