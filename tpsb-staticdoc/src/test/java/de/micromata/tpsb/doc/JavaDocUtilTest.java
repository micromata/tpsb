package de.micromata.tpsb.doc;

import japa.parser.ast.comments.JavadocComment;
import junit.framework.Assert;

import org.junit.Test;

import de.micromata.tpsb.doc.parser.JavaDocInfo;
import de.micromata.tpsb.doc.parser.JavaDocUtil;

public class JavaDocUtilTest
{

  String javaDoc = "\n" + //
      " * Nutzer Einloggen \n"
      + //
      " * \n"
      + //
      " * Loggt einen Nutzer anhand der übergenenen Credentials\n"
      + //
      " * in die Anwendung ein.\n"
      + //
      " * \n"
      + //
      " * @author Stefan Stützer\n"
      + //
      " * @since 1.6\n"
      + //
      " * @see UmgmtIndexPage\n"
      + //
      " * \n"
      + //
      " * @param username Der Nutzername\n"
      + //
      " * @param password\n"
      + //
      " * 					Das Passwort\n"
      + //
      " * 					Dieses wird initial per Zufall vergeben.\n"
      + //
      " * @param pageClass Die erwartete Seite\n"
      + //
      " *\n"
      + //
      " * @return Einen Testbuilder\n";

  String javaDoc2 = "\n" + //
      " * Loggt einen Nutzer anhand der übergenenen Credentials\n"
      + //
      " * in die Anwendung ein.\n"
      + //
      " * \n"
      + //
      " * @author Stefan Stützer\n"
      + //
      " * @since 1.6\n"
      + //
      " * @see UmgmtIndexPage\n"
      + //
      " * \n"
      + //
      " * @param username Der Nutzername\n"
      + //
      " * @param password\n"
      + //
      " * 					Das Passwort\n"
      + //
      " * 					Dieses wird initial per Zufall vergeben.\n"
      + //
      " * @param pageClass Die erwartete Seite\n"
      + //
      " *\n"
      + //
      " * @return Einen Testbuilder\n";

  @Test
  public void testExtractParamJavaDoc()
  {
    JavadocComment javadocComment = new JavadocComment(javaDoc);
    JavaDocInfo jDocInfo = JavaDocUtil.parseJavaDoc(javadocComment);
    Assert.assertEquals("Nutzer Einloggen", jDocInfo.getTitle());
    Assert.assertEquals("Loggt einen Nutzer anhand der übergenenen Credentials\nin die Anwendung ein.",
        jDocInfo.getDescription());
    Assert.assertEquals("Der Nutzername", jDocInfo.getParamDoc("username"));
    Assert.assertEquals("Die erwartete Seite", jDocInfo.getParamDoc("pageClass"));
    Assert.assertEquals("Stefan Stützer", jDocInfo.getTagInfo("@author").iterator().next().getValue());
    Assert.assertEquals("1.6", jDocInfo.getTagInfo("@since").iterator().next().getValue());
    Assert.assertEquals("Einen Testbuilder", jDocInfo.getTagInfo("@return").iterator().next().getValue());
  }

  @Test
  public void testExtractParamJavaDoc2()
  {
    JavadocComment javadocComment = new JavadocComment(javaDoc2);
    JavaDocInfo jDocInfo = JavaDocUtil.parseJavaDoc(javadocComment);
    Assert.assertNull(jDocInfo.getDescription());
    Assert.assertEquals("Loggt einen Nutzer anhand der übergenenen Credentials\nin die Anwendung ein.",
        jDocInfo.getTitle());
  }

}
