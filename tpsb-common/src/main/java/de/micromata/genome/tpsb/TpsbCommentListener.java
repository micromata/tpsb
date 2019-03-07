//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.tpsb;

/**
 * Listener listen to addComment() method.
 * 
 * @author roger
 * 
 */
public interface TpsbCommentListener
{
  /**
   * Add a raw comment to the listener
   * 
   * @param builder the current testbuilder
   * @param expression String expression with optional ${} expressions.
   */
  public void addCommentExpression(TestBuilder< ? > builder, String expression);

  /**
   * Inside addCommentRaw(...) the expression will be evaluated. The evaluated will be call addCommentText()
   * 
   * @param builder the builder where to add the comment to
   * @param text the text to add as a comment
   */
  public void addCommentText(TestBuilder< ? > builder, String text);
}
